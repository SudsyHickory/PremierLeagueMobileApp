package com.example.premierleagueapp.domain.model

/** Jeden wiersz tabeli ligowej w wybranym wariancie ([StandingType]). */
data class StandingRow(
    val position: Int,
    val team: Team,
    val played: Int,
    val goalDifference: Int,
    val points: Int
)

/** Wariant tabeli: ogółem, tylko mecze u siebie, tylko mecze na wyjeździe. */
enum class StandingType {
    TOTAL,
    HOME,
    AWAY
}
