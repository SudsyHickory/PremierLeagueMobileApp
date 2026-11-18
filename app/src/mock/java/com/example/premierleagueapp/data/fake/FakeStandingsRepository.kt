package com.example.premierleagueapp.data.fake

import com.example.premierleagueapp.domain.model.StandingRow
import com.example.premierleagueapp.domain.model.StandingType
import com.example.premierleagueapp.domain.repository.StandingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/** Tabela z makiet trzymana w pamięci. `force` jest ignorowane – fake zawsze „pobiera”. */
@Singleton
class FakeStandingsRepository @Inject constructor(
    private val config: FakeConfig
) : StandingsRepository {

    private val standings = MutableStateFlow(
        if (config.startEmpty) emptyMap() else MockData.standings
    )
    private val lastUpdated = MutableStateFlow(
        if (config.startEmpty) null else config.now()
    )

    override fun observeStandings(type: StandingType): Flow<List<StandingRow>> =
        standings.map { it[type].orEmpty() }

    override fun observeLastUpdated(): Flow<Instant?> = lastUpdated.asStateFlow()

    override suspend fun refresh(force: Boolean) {
        config.simulateRefresh()
        standings.value = MockData.standings
        lastUpdated.value = config.now()
    }
}
