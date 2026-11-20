package com.example.premierleagueapp.ui.standings

import com.example.premierleagueapp.domain.model.RefreshException
import com.example.premierleagueapp.domain.model.StandingRow
import com.example.premierleagueapp.domain.model.StandingType
import java.time.Instant

/**
 * Stan ekranu Tabela. Wzorzec dla pozostałych ekranów ligowych:
 * - [Loading] – brak danych, trwa pierwsze pobieranie (szkielety),
 * - [Content] – dane z repozytorium, także w trakcie odświeżania,
 * - [Error] – odświeżenie się nie udało, a danych nie ma (pełny ekran błędu).
 *
 * Nieudane odświeżenie przy istniejących danych nie zmienia stanu – ViewModel wysyła wtedy
 * [StandingsEvent.RefreshFailed], które ekran pokazuje jako snackbar.
 */
sealed interface StandingsUiState {

    data object Loading : StandingsUiState

    data class Content(
        val rows: List<StandingRow>,
        val type: StandingType,
        val isRefreshing: Boolean,
        val lastUpdated: Instant?,
        /** Sezon do podtytułu; `null`, gdy informacji o lidze jeszcze nie ma. */
        val season: Season?
    ) : StandingsUiState

    data class Error(val reason: RefreshException.Reason) : StandingsUiState
}

/** Sezon w postaci potrzebnej ekranowi, np. 2026/27. */
data class Season(val startYear: Int, val endYear: Int)

/** Jednorazowe zdarzenia ekranu Tabela. */
sealed interface StandingsEvent {
    /** Odświeżenie się nie udało, ale dane są na ekranie – snackbar „Ponów”. */
    data class RefreshFailed(val reason: RefreshException.Reason) : StandingsEvent
}
