package com.example.premierleagueapp.di

import com.example.premierleagueapp.data.fake.FakeCompetitionRepository
import com.example.premierleagueapp.data.fake.FakeConfig
import com.example.premierleagueapp.data.fake.FakeMatchesRepository
import com.example.premierleagueapp.data.fake.FakeStandingsRepository
import com.example.premierleagueapp.domain.repository.CompetitionRepository
import com.example.premierleagueapp.domain.repository.MatchesRepository
import com.example.premierleagueapp.domain.repository.StandingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Powiązania wariantu `mock`: repozytoria ligowe to fake'i z danymi z makiet. */
@Module
@InstallIn(SingletonComponent::class)
abstract class MockRepositoryModule {

    @Binds
    abstract fun bindCompetitionRepository(fake: FakeCompetitionRepository): CompetitionRepository

    @Binds
    abstract fun bindStandingsRepository(fake: FakeStandingsRepository): StandingsRepository

    @Binds
    abstract fun bindMatchesRepository(fake: FakeMatchesRepository): MatchesRepository

    companion object {
        /** Tu zmienia się zachowanie fake'ów w aplikacji: opóźnienie, błąd, start bez danych. */
        @Provides
        @Singleton
        fun provideFakeConfig(): FakeConfig = FakeConfig()
    }
}
