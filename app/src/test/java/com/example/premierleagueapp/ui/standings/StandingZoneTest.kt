package com.example.premierleagueapp.ui.standings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StandingZoneTest {

    @Test
    fun `positions 1 to 4 are Champions League`() {
        (1..4).forEach { assertEquals(StandingZone.CHAMPIONS_LEAGUE, zoneFor(it)) }
    }

    @Test
    fun `position 5 is Europa League`() {
        assertEquals(StandingZone.EUROPA_LEAGUE, zoneFor(5))
    }

    @Test
    fun `mid table has no zone`() {
        (6..17).forEach { assertNull(zoneFor(it)) }
    }

    @Test
    fun `positions 18 to 20 are relegation`() {
        (18..20).forEach { assertEquals(StandingZone.RELEGATION, zoneFor(it)) }
    }

    @Test
    fun `positions outside the table have no zone`() {
        assertNull(zoneFor(0))
        assertNull(zoneFor(21))
    }

    @Test
    fun `goal difference is formatted with a sign`() {
        assertEquals("+12", formatGoalDifference(12))
        assertEquals("0", formatGoalDifference(0))
        assertEquals("-3", formatGoalDifference(-3))
    }
}
