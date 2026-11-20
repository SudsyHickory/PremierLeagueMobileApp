package com.example.premierleagueapp.ui.standings

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.premierleagueapp.data.fake.FakeCompetitionRepository
import com.example.premierleagueapp.data.fake.FakeConfig
import com.example.premierleagueapp.data.fake.FakeStandingsRepository
import com.example.premierleagueapp.data.fake.MockData
import com.example.premierleagueapp.domain.model.RefreshException
import com.example.premierleagueapp.domain.model.StandingType
import com.example.premierleagueapp.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

/**
 * Testy [StandingsViewModel] na prawdziwych fake'ach z wariantu `mock`.
 * Opóźnienie odświeżania to 800 ms wirtualnego czasu.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StandingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fixedNow = Instant.parse("2026-10-05T10:00:00Z")
    private var now = fixedNow

    private fun config(
        startEmpty: Boolean = true,
        failRefresh: RefreshException.Reason? = null
    ) = FakeConfig(
        refreshDelay = 800.milliseconds,
        failRefresh = failRefresh,
        startEmpty = startEmpty,
        now = { now }
    )

    private fun viewModel(
        standingsConfig: FakeConfig = config(),
        competitionConfig: FakeConfig = standingsConfig
    ) = StandingsViewModel(
        standingsRepository = FakeStandingsRepository(standingsConfig),
        competitionRepository = FakeCompetitionRepository(competitionConfig),
        savedStateHandle = SavedStateHandle()
    )

    /** `uiState` startuje dopiero z subskrybentem (`WhileSubscribed`) – w teście trzyma go ten kolektor. */
    private fun TestScope.subscribe(viewModel: StandingsViewModel) {
        backgroundScope.launch { viewModel.uiState.collect {} }
    }

    private fun StandingsViewModel.content() = uiState.value as StandingsUiState.Content

    @Test
    fun `empty start shows Loading then Content after the first refresh`() =
        runTest(mainDispatcherRule.dispatcher) {
            val viewModel = viewModel()
            subscribe(viewModel)
            runCurrent()

            assertEquals(StandingsUiState.Loading, viewModel.uiState.value)

            advanceUntilIdle()

            val content = viewModel.content()
            assertEquals(MockData.standings.getValue(StandingType.TOTAL), content.rows)
            assertEquals(20, content.rows.size)
            assertEquals(StandingType.TOTAL, content.type)
            assertFalse(content.isRefreshing)
            assertEquals(fixedNow, content.lastUpdated)
            assertEquals(Season(2026, 2027), content.season)
        }

    @Test
    fun `cached data is shown immediately`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = viewModel(config(startEmpty = false))
        subscribe(viewModel)
        runCurrent()

        val content = viewModel.content()
        assertEquals(20, content.rows.size)
        assertEquals(fixedNow, content.lastUpdated)
        assertTrue("startowe odświeżenie trwa w tle", content.isRefreshing)
    }

    @Test
    fun `selecting a type shows that variant`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = viewModel(config(startEmpty = false))
        subscribe(viewModel)
        advanceUntilIdle()

        viewModel.onTypeSelected(StandingType.HOME)
        runCurrent()

        assertEquals(StandingType.HOME, viewModel.content().type)
        assertEquals(MockData.standings.getValue(StandingType.HOME), viewModel.content().rows)

        viewModel.onTypeSelected(StandingType.AWAY)
        runCurrent()

        assertEquals(MockData.standings.getValue(StandingType.AWAY), viewModel.content().rows)
    }

    @Test
    fun `refresh toggles isRefreshing and updates the timestamp`() =
        runTest(mainDispatcherRule.dispatcher) {
            val viewModel = viewModel(config(startEmpty = false))
            subscribe(viewModel)
            advanceUntilIdle()
            now = fixedNow.plusSeconds(600)

            viewModel.onRefresh()
            runCurrent()
            assertTrue(viewModel.content().isRefreshing)
            assertEquals(fixedNow, viewModel.content().lastUpdated)

            advanceUntilIdle()
            assertFalse(viewModel.content().isRefreshing)
            assertEquals(fixedNow.plusSeconds(600), viewModel.content().lastUpdated)
        }

    @Test
    fun `failed refresh with data keeps Content and sends RefreshFailed`() =
        runTest(mainDispatcherRule.dispatcher) {
            val config = config(startEmpty = false)
            val viewModel = viewModel(config)
            subscribe(viewModel)
            advanceUntilIdle()

            viewModel.events.test {
                config.failRefresh = RefreshException.Reason.NO_CONNECTION
                viewModel.onRefresh()
                advanceUntilIdle()

                assertEquals(
                    StandingsEvent.RefreshFailed(RefreshException.Reason.NO_CONNECTION),
                    awaitItem()
                )
                val content = viewModel.content()
                assertFalse(content.isRefreshing)
                assertEquals(fixedNow, content.lastUpdated)
                assertEquals(20, content.rows.size)
            }
        }

    @Test
    fun `failed refresh without data shows Error and retry recovers`() =
        runTest(mainDispatcherRule.dispatcher) {
            val config = config(failRefresh = RefreshException.Reason.RATE_LIMITED)
            val viewModel = viewModel(config)
            subscribe(viewModel)

            viewModel.events.test {
                advanceUntilIdle()
                assertEquals(
                    StandingsUiState.Error(RefreshException.Reason.RATE_LIMITED),
                    viewModel.uiState.value
                )
                expectNoEvents()
            }

            config.failRefresh = null
            viewModel.onRetry()
            runCurrent()
            assertEquals(StandingsUiState.Loading, viewModel.uiState.value)

            advanceUntilIdle()
            assertEquals(20, viewModel.content().rows.size)
        }

    @Test
    fun `refresh requested while refreshing is ignored`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = viewModel(config(startEmpty = false))
        subscribe(viewModel)
        advanceUntilIdle()
        val start = currentTime

        viewModel.onRefresh()
        advanceTimeBy(400)
        viewModel.onRefresh()
        advanceUntilIdle()

        assertEquals(800, currentTime - start)
        assertFalse(viewModel.content().isRefreshing)
    }

    @Test
    fun `competition failure does not affect the table`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = viewModel(
            standingsConfig = config(),
            competitionConfig = config(failRefresh = RefreshException.Reason.NO_CONNECTION)
        )
        subscribe(viewModel)
        advanceUntilIdle()

        val content = viewModel.content()
        assertEquals(20, content.rows.size)
        assertNull(content.season)
    }
}
