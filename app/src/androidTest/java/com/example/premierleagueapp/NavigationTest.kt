package com.example.premierleagueapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.premierleagueapp.ui.PremierLeagueApp
import com.example.premierleagueapp.ui.theme.PremierLeagueAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** Sprawdza szkielet z etapu 1: cztery zakładki i przełączanie między nimi. */
class NavigationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setUp() {
        composeRule.setContent {
            PremierLeagueAppTheme {
                PremierLeagueApp()
            }
        }
    }

    private fun tab(label: String) = composeRule.onNode(hasText(label) and isSelectable())

    @Test
    fun startTabIsSelectedOnLaunch() {
        tab("Start").assertIsSelected()
        composeRule.onNodeWithText("Podsumowanie ligi").assertIsDisplayed()
    }

    @Test
    fun allFourTabsAreVisible() {
        listOf("Start", "Tabela", "Mecze", "Kibic").forEach { tab(it).assertIsDisplayed() }
    }

    @Test
    fun clickingTabShowsItsScreen() {
        tab("Tabela").performClick()
        tab("Tabela").assertIsSelected()
        composeRule.onNodeWithText("Tu pojawi się pełna tabela ligowa.").assertIsDisplayed()

        tab("Mecze").performClick()
        tab("Mecze").assertIsSelected()
        composeRule.onNodeWithText("Tu pojawi się terminarz i wyniki kolejki.").assertIsDisplayed()

        tab("Kibic").performClick()
        tab("Kibic").assertIsSelected()
        composeRule.onNodeWithText("Dziennik kibica").assertIsDisplayed()

        tab("Start").performClick()
        tab("Start").assertIsSelected()
        composeRule.onNodeWithText("Podsumowanie ligi").assertIsDisplayed()
    }
}
