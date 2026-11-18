package com.example.premierleagueapp.domain.repository

import com.example.premierleagueapp.domain.model.Competition
import com.example.premierleagueapp.domain.model.RefreshException
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface CompetitionRepository {

    /** Informacje o lidze i sezonie; `null`, gdy danych jeszcze nie ma. */
    fun observeCompetition(): Flow<Competition?>

    /** Czas ostatniego udanego odświeżenia; `null`, gdy danych jeszcze nie ma. */
    fun observeLastUpdated(): Flow<Instant?>

    /**
     * Odświeża dane. Z `force = false` repozytorium może pominąć pobieranie, gdy dane są świeże.
     *
     * @throws RefreshException gdy odświeżenie się nie udało; dotychczasowe dane zostają
     */
    suspend fun refresh(force: Boolean = false)
}
