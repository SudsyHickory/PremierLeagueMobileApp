package com.example.premierleagueapp.ui.standings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premierleagueapp.domain.model.RefreshException
import com.example.premierleagueapp.domain.model.StandingType
import com.example.premierleagueapp.domain.repository.CompetitionRepository
import com.example.premierleagueapp.domain.repository.StandingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StandingsViewModel @Inject constructor(
    private val standingsRepository: StandingsRepository,
    private val competitionRepository: CompetitionRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val selectedType = savedStateHandle.getStateFlow(KEY_TYPE, StandingType.TOTAL)
    private val isRefreshing = MutableStateFlow(false)

    /** Powód ostatniego nieudanego odświeżenia, gdy danych nie było; czyszczony przy kolejnej próbie. */
    private val emptyError = MutableStateFlow<RefreshException.Reason?>(null)

    private val eventChannel = Channel<StandingsEvent>(Channel.BUFFERED)
    val events: Flow<StandingsEvent> = eventChannel.receiveAsFlow()

    // Wariant i jego wiersze idą w parze, żeby ekran nie pokazał np. typu HOME z wierszami TOTAL.
    private val selectedRows = selectedType.flatMapLatest { type ->
        standingsRepository.observeStandings(type).map { rows -> type to rows }
    }

    private val season = competitionRepository.observeCompetition().map { competition ->
        competition?.let { Season(it.seasonStartYear, it.seasonEndYear) }
    }

    val uiState: StateFlow<StandingsUiState> = combine(
        selectedRows,
        standingsRepository.observeLastUpdated(),
        season,
        isRefreshing,
        emptyError
    ) { (type, rows), lastUpdated, season, refreshing, error ->
        when {
            rows.isNotEmpty() -> StandingsUiState.Content(
                rows = rows,
                type = type,
                isRefreshing = refreshing,
                lastUpdated = lastUpdated,
                season = season
            )
            error != null -> StandingsUiState.Error(error)
            else -> StandingsUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StandingsUiState.Loading
    )

    init {
        refresh(force = false)
        // Sezon jest tylko dodatkiem do podtytułu – jego błąd nie wpływa na stan tabeli.
        viewModelScope.launch {
            try {
                competitionRepository.refresh(force = false)
            } catch (_: RefreshException) {
            }
        }
    }

    /** Pull-to-refresh lub przycisk w pasku – wymusza pobranie danych. */
    fun onRefresh() = refresh(force = true)

    /** „Spróbuj ponownie” na pełnym ekranie błędu. */
    fun onRetry() = refresh(force = true)

    fun onTypeSelected(type: StandingType) {
        savedStateHandle[KEY_TYPE] = type
    }

    private fun refresh(force: Boolean) {
        if (isRefreshing.value) return
        isRefreshing.value = true
        emptyError.value = null
        viewModelScope.launch {
            try {
                standingsRepository.refresh(force)
            } catch (e: RefreshException) {
                val hasData = standingsRepository.observeStandings(selectedType.value)
                    .first()
                    .isNotEmpty()
                if (hasData) {
                    eventChannel.send(StandingsEvent.RefreshFailed(e.reason))
                } else {
                    emptyError.value = e.reason
                }
            } finally {
                isRefreshing.value = false
            }
        }
    }

    private companion object {
        const val KEY_TYPE = "standings_type"
    }
}
