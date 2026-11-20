package com.example.premierleagueapp.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class UpdatedAgoTest {

    private val now = Instant.parse("2026-10-05T12:00:00Z")

    private fun ago(seconds: Long) = updatedAgo(now.minusSeconds(seconds), now)

    @Test
    fun `under a minute is just now`() {
        assertEquals(UpdatedAgo.JustNow, ago(0))
        assertEquals(UpdatedAgo.JustNow, ago(59))
    }

    @Test
    fun `minutes below an hour`() {
        assertEquals(UpdatedAgo.Minutes(4), ago(4 * 60 + 30))
        assertEquals(UpdatedAgo.Minutes(59), ago(59 * 60 + 59))
    }

    @Test
    fun `hours below a day`() {
        assertEquals(UpdatedAgo.Hours(1), ago(60 * 60))
        assertEquals(UpdatedAgo.Hours(2), ago(2 * 60 * 60 + 10 * 60))
    }

    @Test
    fun `days from 24 hours`() {
        assertEquals(UpdatedAgo.Days(1), ago(24 * 60 * 60))
        assertEquals(UpdatedAgo.Days(3), ago(3 * 24 * 60 * 60 + 5))
    }

    @Test
    fun `timestamp from the future is just now`() {
        assertEquals(UpdatedAgo.JustNow, updatedAgo(now.plusSeconds(120), now))
    }
}
