package com.example.premierleagueapp.ui.standings

/** Strefa tabeli wyróżniana kolorem numeru pozycji. */
enum class StandingZone {
    CHAMPIONS_LEAGUE,
    EUROPA_LEAGUE,
    RELEGATION
}

private const val CHAMPIONS_LEAGUE_PLACES = 4
private const val EUROPA_LEAGUE_PLACES = 1
private const val RELEGATION_PLACES = 3

/**
 * Strefa dla pozycji w tabeli: 1–4 Liga Mistrzów, 5 Liga Europy, trzy ostatnie miejsca – spadek.
 * `null` – środek tabeli.
 */
fun zoneFor(position: Int, totalTeams: Int = 20): StandingZone? = when {
    position < 1 || position > totalTeams -> null
    position <= CHAMPIONS_LEAGUE_PLACES -> StandingZone.CHAMPIONS_LEAGUE
    position <= CHAMPIONS_LEAGUE_PLACES + EUROPA_LEAGUE_PLACES -> StandingZone.EUROPA_LEAGUE
    position > totalTeams - RELEGATION_PLACES -> StandingZone.RELEGATION
    else -> null
}
