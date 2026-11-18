package com.example.premierleagueapp.domain.model

import java.time.Instant

/**
 * Mecz ligowy.
 *
 * @property matchday numer kolejki (1–38)
 * @property kickoff początek meczu; strefę czasową dobiera dopiero warstwa UI
 * @property homeScore bramki gospodarzy; `null`, dopóki mecz nie ma wyniku
 * @property awayScore bramki gości; `null`, dopóki mecz nie ma wyniku
 */
data class Match(
    val id: Int,
    val matchday: Int,
    val kickoff: Instant,
    val status: MatchStatus,
    val homeTeam: Team,
    val awayTeam: Team,
    val homeScore: Int? = null,
    val awayScore: Int? = null
)

enum class MatchStatus {
    SCHEDULED,
    IN_PLAY,
    FINISHED,
    POSTPONED
}
