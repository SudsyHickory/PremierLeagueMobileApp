package com.example.premierleagueapp.domain.model

import java.time.Instant

/**
 * Wspomnienie z meczu w Dzienniku kibica.
 *
 * @property id identyfikator wpisu; `0` oznacza wpis jeszcze niezapisany
 * @property match kopia danych meczu z chwili utworzenia wpisu – później się nie zmienia,
 *   więc wspomnienie jest czytelne także wtedy, gdy danych ligowych nie ma
 * @property voiceNotePath ścieżka pliku notatki głosowej; `null`, gdy wpis jej nie ma
 * @property address adres miejsca (z Geocodera lub wpisany ręcznie)
 */
data class Memory(
    val id: Long = 0,
    val match: Match,
    val createdAt: Instant,
    val media: List<MemoryMedia> = emptyList(),
    val voiceNotePath: String? = null,
    val voiceNoteDurationMs: Long? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

/**
 * Zdjęcie lub film należący do wspomnienia.
 *
 * @property path ścieżka pliku w prywatnym katalogu aplikacji
 * @property durationMs długość filmu; `null` dla zdjęć
 */
data class MemoryMedia(
    val id: Long = 0,
    val type: MediaType,
    val path: String,
    val durationMs: Long? = null
)

enum class MediaType {
    PHOTO,
    VIDEO
}
