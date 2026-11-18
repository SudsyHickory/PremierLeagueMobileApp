package com.example.premierleagueapp.domain.repository

import com.example.premierleagueapp.domain.model.Memory
import kotlinx.coroutines.flow.Flow

/** Wspomnienia użytkownika. Implementacja powstaje w etapie 7a, po dodaniu bazy Room. */
interface MemoriesRepository {

    /** Wszystkie wspomnienia, od najnowszego. */
    fun observeMemories(): Flow<List<Memory>>

    /** Jedno wspomnienie; `null`, gdy wpis nie istnieje (np. został usunięty). */
    fun observeMemory(id: Long): Flow<Memory?>

    /** Zapisuje nowy wpis (`id == 0`) lub aktualizuje istniejący. Zwraca identyfikator wpisu. */
    suspend fun save(memory: Memory): Long

    /** Usuwa wpis razem z jego plikami. */
    suspend fun delete(id: Long)
}
