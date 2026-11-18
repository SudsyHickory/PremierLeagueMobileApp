package com.example.premierleagueapp.domain.repository

import com.example.premierleagueapp.domain.model.Match
import com.example.premierleagueapp.domain.model.RefreshException
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface MatchesRepository {

    /** Wszystkie znane mecze sezonu, od najwcześniejszego. Pusta lista, gdy danych jeszcze nie ma. */
    fun observeMatches(): Flow<List<Match>>

    /** Mecze jednej kolejki, od najwcześniejszego. */
    fun observeMatchday(matchday: Int): Flow<List<Match>>

    /** Czas ostatniego udanego odświeżenia; `null`, gdy danych jeszcze nie ma. */
    fun observeLastUpdated(): Flow<Instant?>

    /**
     * Odświeża dane. Z `force = false` repozytorium może pominąć pobieranie, gdy dane są świeże.
     *
     * @throws RefreshException gdy odświeżenie się nie udało; dotychczasowe dane zostają
     */
    suspend fun refresh(force: Boolean = false)
}
