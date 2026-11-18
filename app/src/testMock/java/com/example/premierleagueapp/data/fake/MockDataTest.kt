package com.example.premierleagueapp.data.fake

import com.example.premierleagueapp.domain.model.MatchStatus
import com.example.premierleagueapp.domain.model.StandingType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pilnuje spójności danych testowych, na których powstają ekrany. */
class MockDataTest {

    @Test
    fun `every standings variant has 20 teams in positions 1 to 20`() {
        StandingType.entries.forEach { type ->
            val rows = MockData.standings.getValue(type)
            assertEquals("$type positions", (1..20).toList(), rows.map { it.position })
            assertEquals("$type distinct teams", 20, rows.map { it.team.id }.distinct().size)
        }
    }

    @Test
    fun `total table matches the mockup at both ends`() {
        val total = MockData.standings.getValue(StandingType.TOTAL)

        val leader = total.first()
        assertEquals("ARS", leader.team.tla)
        assertEquals(8, leader.played)
        assertEquals(12, leader.goalDifference)
        assertEquals(20, leader.points)

        val eighteenth = total[17]
        assertEquals("LEE", eighteenth.team.tla)
        assertEquals(-8, eighteenth.goalDifference)
        assertEquals(5, eighteenth.points)
    }

    @Test
    fun `goal differences in the total table sum to zero`() {
        assertEquals(0, MockData.standings.getValue(StandingType.TOTAL).sumOf { it.goalDifference })
    }

    @Test
    fun `home and away tables add up to the total table`() {
        val home = MockData.standings.getValue(StandingType.HOME).associateBy { it.team.id }
        val away = MockData.standings.getValue(StandingType.AWAY).associateBy { it.team.id }

        MockData.standings.getValue(StandingType.TOTAL).forEach { total ->
            val h = home.getValue(total.team.id)
            val a = away.getValue(total.team.id)
            assertEquals(total.team.tla, total.played, h.played + a.played)
            assertEquals(total.team.tla, total.goalDifference, h.goalDifference + a.goalDifference)
            assertEquals(total.team.tla, total.points, h.points + a.points)
        }
    }

    @Test
    fun `home and away tables are ordered by points then goal difference`() {
        listOf(StandingType.HOME, StandingType.AWAY).forEach { type ->
            MockData.standings.getValue(type).zipWithNext { upper, lower ->
                assertTrue(
                    "$type: ${upper.team.tla} above ${lower.team.tla}",
                    upper.points > lower.points ||
                        (upper.points == lower.points && upper.goalDifference >= lower.goalDifference)
                )
            }
        }
    }

    @Test
    fun `each mocked matchday has 10 matches and every team plays once`() {
        listOf(7, 8, 9).forEach { matchday ->
            val matches = MockData.matches.filter { it.matchday == matchday }
            assertEquals("matchday $matchday", 10, matches.size)
            val teams = matches.flatMap { listOf(it.homeTeam.id, it.awayTeam.id) }
            assertEquals("matchday $matchday teams", 20, teams.distinct().size)
        }
    }

    @Test
    fun `matchday 8 is finished with scores and matchday 9 is scheduled without`() {
        MockData.matches.filter { it.matchday == 8 }.forEach {
            assertEquals(MatchStatus.FINISHED, it.status)
            assertNotNull(it.homeScore)
            assertNotNull(it.awayScore)
        }
        MockData.matches.filter { it.matchday == 9 }.forEach {
            assertEquals(MatchStatus.SCHEDULED, it.status)
            assertNull(it.homeScore)
            assertNull(it.awayScore)
        }
    }

    @Test
    fun `matches are sorted by kickoff and have unique ids`() {
        assertEquals(MockData.matches.sortedBy { it.kickoff }, MockData.matches)
        assertEquals(MockData.matches.size, MockData.matches.map { it.id }.distinct().size)
    }

    @Test
    fun `current matchday is one of the mocked matchdays`() {
        assertEquals(8, MockData.competition.currentMatchday)
        assertTrue(MockData.matches.any { it.matchday == MockData.competition.currentMatchday })
    }
}
