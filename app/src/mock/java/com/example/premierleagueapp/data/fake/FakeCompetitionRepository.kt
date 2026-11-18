package com.example.premierleagueapp.data.fake

import com.example.premierleagueapp.domain.model.Competition
import com.example.premierleagueapp.domain.repository.CompetitionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/** Informacje o lidze z makiet trzymane w pamięci. `force` jest ignorowane – fake zawsze „pobiera”. */
@Singleton
class FakeCompetitionRepository @Inject constructor(
    private val config: FakeConfig
) : CompetitionRepository {

    private val competition = MutableStateFlow(
        if (config.startEmpty) null else MockData.competition
    )
    private val lastUpdated = MutableStateFlow(
        if (config.startEmpty) null else config.now()
    )

    override fun observeCompetition(): Flow<Competition?> = competition.asStateFlow()

    override fun observeLastUpdated(): Flow<Instant?> = lastUpdated.asStateFlow()

    override suspend fun refresh(force: Boolean) {
        config.simulateRefresh()
        competition.value = MockData.competition
        lastUpdated.value = config.now()
    }
}
