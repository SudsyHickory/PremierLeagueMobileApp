package com.example.premierleagueapp

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

/**
 * Sprawdza szkielet aplikacji: cztery zakładki i przełączanie między nimi.
 * Uruchamia prawdziwą [MainActivity], bo od etapu 3 ekrany pobierają ViewModele z Hilt.
 */
@OptIn(ExperimentalTestApi::class)
class NavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

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
        // Fake tabeli startuje pusty i wypełnia się po symulowanym pobieraniu (800 ms).
        composeRule.waitUntilAtLeastOneExists(hasText("Ogółem"), timeoutMillis = 5_000)
        composeRule.onNodeWithText("Arsenal").assertIsDisplayed()

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
