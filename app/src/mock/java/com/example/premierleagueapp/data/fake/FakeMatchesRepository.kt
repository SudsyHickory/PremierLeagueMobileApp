package com.example.premierleagueapp.data.fake

import com.example.premierleagueapp.domain.model.Match
import com.example.premierleagueapp.domain.repository.MatchesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/** Mecze z makiet trzymane w pamięci. `force` jest ignorowane – fake zawsze „pobiera”. */
@Singleton
class FakeMatchesRepository @Inject constructor(
    private val config: FakeConfig
) : MatchesRepository {

    private val matches = MutableStateFlow(
        if (config.startEmpty) emptyList() else MockData.matches
    )
    private val lastUpdated = MutableStateFlow(
        if (config.startEmpty) null else config.now()
    )

    override fun observeMatches(): Flow<List<Match>> = matches.asStateFlow()

    override fun observeMatchday(matchday: Int): Flow<List<Match>> =
        matches.map { all -> all.filter { it.matchday == matchday } }

    override fun observeLastUpdated(): Flow<Instant?> = lastUpdated.asStateFlow()

    override suspend fun refresh(force: Boolean) {
        config.simulateRefresh()
        matches.value = MockData.matches
        lastUpdated.value = config.now()
    }
}
