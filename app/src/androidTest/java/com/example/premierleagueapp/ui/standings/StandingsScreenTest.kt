package com.example.premierleagueapp.ui.standings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.premierleagueapp.domain.model.RefreshException
import com.example.premierleagueapp.domain.model.StandingType
import com.example.premierleagueapp.ui.theme.PremierLeagueAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Testy bezstanowego [StandingsScreen] – bez ViewModelu i bez Hilt. */
class StandingsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setScreen(
        uiState: StandingsUiState,
        onRetry: () -> Unit = {},
        onTypeSelected: (StandingType) -> Unit = {}
    ) {
        composeRule.setContent {
            PremierLeagueAppTheme {
                StandingsScreen(
                    uiState = uiState,
                    snackbarHostState = SnackbarHostState(),
                    onRefresh = {},
                    onRetry = onRetry,
                    onTypeSelected = onTypeSelected
                )
            }
        }
    }

    @Test
    fun errorStateShowsRetryThatCallsOnRetry() {
        var retries = 0
        setScreen(StandingsUiState.Error(RefreshException.Reason.NO_CONNECTION), onRetry = { retries++ })

        composeRule.onNodeWithText("Brak połączenia").assertIsDisplayed()
        composeRule.onNodeWithText("Spróbuj ponownie").performClick()

        assertEquals(1, retries)
    }

    @Test
    fun contentShowsRowsSeasonAndSelectedType() {
        var selected: StandingType? = null
        setScreen(
            StandingsUiState.Content(
                rows = PreviewStandingRows,
                type = StandingType.TOTAL,
                isRefreshing = false,
                lastUpdated = null,
                season = Season(2026, 2027)
            ),
            onTypeSelected = { selected = it }
        )

        composeRule.onNodeWithText("Tabela • sezon 2026/27").assertIsDisplayed()
        composeRule.onNodeWithText("Arsenal").assertIsDisplayed()
        composeRule.onNodeWithText("Ogółem").assertIsSelected()

        composeRule.onNodeWithText("U siebie").performClick()
        assertEquals(StandingType.HOME, selected)
    }
}
