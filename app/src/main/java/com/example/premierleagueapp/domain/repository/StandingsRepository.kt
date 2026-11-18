package com.example.premierleagueapp.domain.repository

import com.example.premierleagueapp.domain.model.RefreshException
import com.example.premierleagueapp.domain.model.StandingRow
import com.example.premierleagueapp.domain.model.StandingType
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface StandingsRepository {

    /** Tabela w wybranym wariancie, posortowana po pozycji. Pusta lista, gdy danych jeszcze nie ma. */
    fun observeStandings(type: StandingType): Flow<List<StandingRow>>

    /** Czas ostatniego udanego odświeżenia; `null`, gdy danych jeszcze nie ma. */
    fun observeLastUpdated(): Flow<Instant?>

    /**
     * Odświeża dane. Z `force = false` repozytorium może pominąć pobieranie, gdy dane są świeże.
     *
     * @throws RefreshException gdy odświeżenie się nie udało; dotychczasowe dane zostają
     */
    suspend fun refresh(force: Boolean = false)
}
