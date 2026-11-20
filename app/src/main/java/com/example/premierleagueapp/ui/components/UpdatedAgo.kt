package com.example.premierleagueapp.ui.components

import java.time.Duration
import java.time.Instant

/** Wiek danych zaokrąglony do jednostki pokazywanej w „Zaktualizowano X temu”. */
sealed interface UpdatedAgo {
    data object JustNow : UpdatedAgo
    data class Minutes(val value: Int) : UpdatedAgo
    data class Hours(val value: Int) : UpdatedAgo
    data class Days(val value: Int) : UpdatedAgo
}

/**
 * Wylicza wiek danych. Czysta funkcja – testowana bez Compose.
 * Czas z przyszłości (np. przestawiony zegar) traktujemy jak „przed chwilą”.
 */
fun updatedAgo(lastUpdated: Instant, now: Instant): UpdatedAgo {
    val age = Duration.between(lastUpdated, now)
    if (age.isNegative) return UpdatedAgo.JustNow
    return when {
        age.toMinutes() < 1 -> UpdatedAgo.JustNow
        age.toHours() < 1 -> UpdatedAgo.Minutes(age.toMinutes().toInt())
        age.toDays() < 1 -> UpdatedAgo.Hours(age.toHours().toInt())
        else -> UpdatedAgo.Days(age.toDays().toInt())
    }
}
