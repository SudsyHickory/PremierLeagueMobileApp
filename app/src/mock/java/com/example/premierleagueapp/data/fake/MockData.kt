package com.example.premierleagueapp.data.fake

import com.example.premierleagueapp.domain.model.Competition
import com.example.premierleagueapp.domain.model.Match
import com.example.premierleagueapp.domain.model.MatchStatus
import com.example.premierleagueapp.domain.model.StandingRow
import com.example.premierleagueapp.domain.model.StandingType
import com.example.premierleagueapp.domain.model.Team
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Dane testowe przygotowane na podstawie makiet (kolejka 8, sezon 2026/27).
 * To, czego na makietach nie widać (miejsca 19–20, tabele u siebie / na wyjeździe,
 * kolejka 7 i większość kolejki 9), zostało dopisane.
 */
object MockData {

    private val ARS = Team(57, "Arsenal", "Arsenal", "ARS")
    private val AVL = Team(58, "Aston Villa", "Aston Villa", "AVL")
    private val BOU = Team(1044, "Bournemouth", "Bournemouth", "BOU")
    private val BRE = Team(402, "Brentford", "Brentford", "BRE")
    private val BHA = Team(397, "Brighton & Hove Albion", "Brighton", "BHA")
    private val BUR = Team(328, "Burnley", "Burnley", "BUR")
    private val CHE = Team(61, "Chelsea", "Chelsea", "CHE")
    private val CRY = Team(354, "Crystal Palace", "Crystal Palace", "CRY")
    private val EVE = Team(62, "Everton", "Everton", "EVE")
    private val FUL = Team(63, "Fulham", "Fulham", "FUL")
    private val LEE = Team(341, "Leeds United", "Leeds", "LEE")
    private val LIV = Team(64, "Liverpool", "Liverpool", "LIV")
    private val MCI = Team(65, "Manchester City", "Man City", "MCI")
    private val MUN = Team(66, "Manchester United", "Man United", "MUN")
    private val NEW = Team(67, "Newcastle United", "Newcastle", "NEW")
    private val NFO = Team(351, "Nottingham Forest", "Nottm Forest", "NFO")
    private val SUN = Team(71, "Sunderland", "Sunderland", "SUN")
    private val TOT = Team(73, "Tottenham Hotspur", "Tottenham", "TOT")
    private val WHU = Team(563, "West Ham United", "West Ham", "WHU")
    private val WOL = Team(76, "Wolverhampton Wanderers", "Wolves", "WOL")

    val competition = Competition(
        name = "Premier League",
        code = "PL",
        seasonStartYear = 2026,
        seasonEndYear = 2027,
        currentMatchday = 8,
        totalMatchdays = 38
    )

    // ---------- Tabela ----------

    /** Dorobek drużyny osobno u siebie i na wyjeździe; tabela ogólna to ich suma. */
    private class Seed(
        val team: Team,
        val homeGoalDifference: Int,
        val homePoints: Int,
        val awayGoalDifference: Int,
        val awayPoints: Int
    )

    private const val HOME_GAMES = 4
    private const val AWAY_GAMES = 4

    /** W kolejności tabeli ogólnej z makiety (suma kolumn daje wartości z makiety). */
    private val seeds = listOf(
        Seed(ARS, 8, 12, 4, 8),    // +12, 20 pkt
        Seed(MCI, 7, 10, 4, 9),    // +11, 19
        Seed(LIV, 6, 10, 2, 7),    //  +8, 17
        Seed(CHE, 4, 9, 1, 6),     //  +5, 15
        Seed(AVL, 3, 8, 0, 6),     //  +3, 14
        Seed(NEW, 5, 10, -1, 4),   //  +4, 14
        Seed(TOT, 2, 7, 0, 6),     //  +2, 13
        Seed(BHA, 2, 7, -1, 5),    //  +1, 12
        Seed(MUN, 2, 8, -2, 4),    //   0, 12
        Seed(BOU, 1, 7, -2, 4),    //  -1, 11
        Seed(CRY, 0, 5, 0, 6),     //   0, 11
        Seed(FUL, 1, 7, -3, 3),    //  -2, 10
        Seed(BRE, 0, 6, -3, 3),    //  -3,  9
        Seed(NFO, -1, 4, 0, 5),    //  -1,  9
        Seed(EVE, -1, 5, -3, 3),   //  -4,  8
        Seed(WHU, -1, 6, -4, 1),   //  -5,  7
        Seed(WOL, -3, 4, -4, 2),   //  -7,  6
        Seed(LEE, -5, 2, -3, 3),   //  -8,  5
        Seed(BUR, -3, 3, -4, 1),   //  -7,  4
        Seed(SUN, -4, 2, -4, 1)    //  -8,  3
    )

    val standings: Map<StandingType, List<StandingRow>> = mapOf(
        // Kolejność ogólna jest przepisana z makiety, więc nie jest tu sortowana.
        StandingType.TOTAL to seeds.mapIndexed { index, seed ->
            StandingRow(
                position = index + 1,
                team = seed.team,
                played = HOME_GAMES + AWAY_GAMES,
                goalDifference = seed.homeGoalDifference + seed.awayGoalDifference,
                points = seed.homePoints + seed.awayPoints
            )
        },
        StandingType.HOME to ranked(HOME_GAMES, { it.homeGoalDifference }, { it.homePoints }),
        StandingType.AWAY to ranked(AWAY_GAMES, { it.awayGoalDifference }, { it.awayPoints })
    )

    /** Tabela częściowa: punkty, potem bilans; przy remisie decyduje kolejność z tabeli ogólnej. */
    private fun ranked(
        played: Int,
        goalDifference: (Seed) -> Int,
        points: (Seed) -> Int
    ): List<StandingRow> = seeds
        .sortedWith(compareByDescending(points).thenByDescending(goalDifference))
        .mapIndexed { index, seed ->
            StandingRow(
                position = index + 1,
                team = seed.team,
                played = played,
                goalDifference = goalDifference(seed),
                points = points(seed)
            )
        }

    // ---------- Mecze ----------

    /** Godziny na makietach są podane w czasie polskim. */
    private val zone = ZoneId.of("Europe/Warsaw")

    private fun kickoff(month: Int, day: Int, hour: Int, minute: Int): Instant =
        LocalDateTime.of(2026, month, day, hour, minute).atZone(zone).toInstant()

    private fun played(
        id: Int, matchday: Int, kickoff: Instant,
        home: Team, homeScore: Int, awayScore: Int, away: Team
    ) = Match(id, matchday, kickoff, MatchStatus.FINISHED, home, away, homeScore, awayScore)

    private fun upcoming(id: Int, matchday: Int, kickoff: Instant, home: Team, away: Team) =
        Match(id, matchday, kickoff, MatchStatus.SCHEDULED, home, away)

    /** Kolejka 7 – dopisana, żeby na ekranie Mecze dało się cofnąć o kolejkę. */
    private val matchday7 = listOf(
        played(701, 7, kickoff(9, 26, 13, 30), CHE, 1, 1, MCI),
        played(702, 7, kickoff(9, 26, 16, 0), TOT, 2, 0, NEW),
        played(703, 7, kickoff(9, 26, 16, 0), AVL, 2, 1, BHA),
        played(704, 7, kickoff(9, 26, 16, 0), WOL, 0, 2, LIV),
        played(705, 7, kickoff(9, 26, 16, 0), FUL, 1, 0, EVE),
        played(706, 7, kickoff(9, 26, 18, 30), BRE, 2, 2, MUN),
        played(707, 7, kickoff(9, 27, 15, 0), BUR, 0, 3, ARS),
        played(708, 7, kickoff(9, 27, 15, 0), NFO, 1, 1, BOU),
        played(709, 7, kickoff(9, 27, 15, 0), LEE, 1, 0, WHU),
        played(710, 7, kickoff(9, 27, 17, 30), SUN, 1, 2, CRY)
    )

    /** Kolejka 8 – wyniki z makiety ekranu Mecze. */
    private val matchday8 = listOf(
        played(801, 8, kickoff(10, 3, 13, 30), ARS, 2, 1, CHE),
        played(802, 8, kickoff(10, 3, 16, 0), LIV, 0, 0, TOT),
        played(803, 8, kickoff(10, 3, 16, 0), MCI, 3, 1, AVL),
        played(804, 8, kickoff(10, 3, 16, 0), NEW, 1, 0, WOL),
        played(805, 8, kickoff(10, 3, 18, 30), MUN, 2, 2, FUL),
        played(806, 8, kickoff(10, 3, 21, 0), BHA, 1, 1, BRE),
        played(807, 8, kickoff(10, 4, 15, 0), BOU, 2, 0, BUR),
        played(808, 8, kickoff(10, 4, 15, 0), CRY, 0, 1, NFO),
        played(809, 8, kickoff(10, 4, 15, 0), EVE, 1, 2, LEE),
        played(810, 8, kickoff(10, 4, 17, 30), WHU, 3, 2, SUN)
    )

    /** Kolejka 9 – trzy pierwsze mecze to „Najbliższe mecze” z makiety ekranu Start. */
    private val matchday9 = listOf(
        upcoming(901, 9, kickoff(10, 17, 16, 0), NEW, MUN),
        upcoming(902, 9, kickoff(10, 17, 18, 30), BHA, WHU),
        upcoming(903, 9, kickoff(10, 18, 17, 30), EVE, BOU),
        upcoming(904, 9, kickoff(10, 18, 17, 30), CHE, LIV),
        upcoming(905, 9, kickoff(10, 18, 17, 30), TOT, MCI),
        upcoming(906, 9, kickoff(10, 18, 17, 30), AVL, ARS),
        upcoming(907, 9, kickoff(10, 18, 20, 0), WOL, CRY),
        upcoming(908, 9, kickoff(10, 18, 20, 0), FUL, BRE),
        upcoming(909, 9, kickoff(10, 18, 20, 0), NFO, LEE),
        upcoming(910, 9, kickoff(10, 18, 20, 0), BUR, SUN)
    )

    /** Wszystkie mecze, od najwcześniejszego. Pozostałych kolejek sezonu mock nie zawiera. */
    val matches: List<Match> = matchday7 + matchday8 + matchday9
}
