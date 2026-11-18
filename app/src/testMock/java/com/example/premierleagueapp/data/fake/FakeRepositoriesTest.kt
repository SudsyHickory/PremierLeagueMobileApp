package com.example.premierleagueapp.data.fake

import app.cash.turbine.test
import com.example.premierleagueapp.domain.model.RefreshException
import com.example.premierleagueapp.domain.model.StandingType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class FakeRepositoriesTest {

    private val fixedNow = Instant.parse("2026-10-05T10:00:00Z")

    private fun config(
        startEmpty: Boolean = true,
        failRefresh: RefreshException.Reason? = null
    ) = FakeConfig(
        refreshDelay = 800.milliseconds,
        failRefresh = failRefresh,
        startEmpty = startEmpty,
        now = { fixedNow }
    )

    @Test
    fun `standings start empty and fill on first refresh`() = runTest {
        val repository = FakeStandingsRepository(config())

        repository.observeStandings(StandingType.TOTAL).test {
            assertTrue(awaitItem().isEmpty())
            assertNull(repository.observeLastUpdated().first())

            repository.refresh()

            assertEquals(20, awaitItem().size)
            assertEquals(fixedNow, repository.observeLastUpdated().first())
        }
    }

    @Test
    fun `standings emit the requested variant`() = runTest {
        val repository = FakeStandingsRepository(config(startEmpty = false))

        StandingType.entries.forEach { type ->
            assertEquals(MockData.standings.getValue(type), repository.observeStandings(type).first())
        }
    }

    @Test
    fun `matches start empty and fill on first refresh`() = runTest {
        val repository = FakeMatchesRepository(config())

        repository.observeMatches().test {
            assertTrue(awaitItem().isEmpty())

            repository.refresh()

            assertEquals(MockData.matches, awaitItem())
            assertEquals(fixedNow, repository.observeLastUpdated().first())
        }
    }

    @Test
    fun `matchday flow returns only that matchday`() = runTest {
        val repository = FakeMatchesRepository(config(startEmpty = false))

        val matchday8 = repository.observeMatchday(8).first()
        assertEquals(10, matchday8.size)
        assertTrue(matchday8.all { it.matchday == 8 })

        assertTrue(repository.observeMatchday(20).first().isEmpty())
    }

    @Test
    fun `competition starts null and fills on first refresh`() = runTest {
        val repository = FakeCompetitionRepository(config())

        repository.observeCompetition().test {
            assertNull(awaitItem())

            repository.refresh()

            assertEquals(MockData.competition, awaitItem())
        }
    }

    @Test
    fun `startEmpty false gives data and a timestamp immediately`() = runTest {
        val repository = FakeCompetitionRepository(config(startEmpty = false))

        assertEquals(MockData.competition, repository.observeCompetition().first())
        assertEquals(fixedNow, repository.observeLastUpdated().first())
    }

    @Test
    fun `refresh takes the configured delay`() = runTest {
        val repository = FakeStandingsRepository(config())

        repository.refresh(force = true)

        assertEquals(800, currentTime)
    }

    @Test
    fun `failed refresh throws and leaves an empty repository empty`() = runTest {
        val repository = FakeStandingsRepository(
            config(failRefresh = RefreshException.Reason.NO_CONNECTION)
        )

        try {
            repository.refresh()
            fail("refresh() should throw")
        } catch (e: RefreshException) {
            assertEquals(RefreshException.Reason.NO_CONNECTION, e.reason)
        }

        assertTrue(repository.observeStandings(StandingType.TOTAL).first().isEmpty())
        assertNull(repository.observeLastUpdated().first())
    }

    @Test
    fun `failed refresh keeps existing data and its timestamp`() = runTest {
        var now = fixedNow
        val config = FakeConfig(startEmpty = false, now = { now })
        val repository = FakeMatchesRepository(config)

        now = fixedNow.plusSeconds(3600)
        config.failRefresh = RefreshException.Reason.RATE_LIMITED

        try {
            repository.refresh(force = true)
            fail("refresh() should throw")
        } catch (e: RefreshException) {
            assertEquals(RefreshException.Reason.RATE_LIMITED, e.reason)
        }

        assertEquals(MockData.matches, repository.observeMatches().first())
        assertEquals(fixedNow, repository.observeLastUpdated().first())
    }

    @Test
    fun `refresh works again after the failure is switched off`() = runTest {
        val config = config(failRefresh = RefreshException.Reason.NO_CONNECTION)
        val repository = FakeCompetitionRepository(config)

        runCatching { repository.refresh() }
        config.failRefresh = null
        repository.refresh()

        assertEquals(MockData.competition, repository.observeCompetition().first())
    }
}
