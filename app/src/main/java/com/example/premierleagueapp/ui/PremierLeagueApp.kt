package com.example.premierleagueapp.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.premierleagueapp.ui.matches.MatchesScreen
import com.example.premierleagueapp.ui.memories.MemoriesScreen
import com.example.premierleagueapp.ui.navigation.TopLevelDestination
import com.example.premierleagueapp.ui.standings.StandingsScreen
import com.example.premierleagueapp.ui.start.StartScreen

/**
 * Wspólny szkielet aplikacji: dolny pasek nawigacji i [NavHost] z czterema zakładkami.
 * Górny pasek należy do poszczególnych ekranów, bo jego treść zależy od ekranu.
 */
@Composable
fun PremierLeagueApp(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.START.route,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable(TopLevelDestination.START.route) { StartScreen() }
            composable(TopLevelDestination.STANDINGS.route) { StandingsScreen() }
            composable(TopLevelDestination.MATCHES.route) { MatchesScreen() }
            composable(TopLevelDestination.MEMORIES.route) { MemoriesScreen() }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    NavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            val selected = currentDestination?.hierarchy
                ?.any { it.route == destination.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigateToTopLevel(destination) },
                icon = {
                    Icon(
                        painter = painterResource(destination.iconRes),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(destination.labelRes)) }
            )
        }
    }
}

/**
 * Przełącza zakładkę tak, by na stosie był zawsze tylko jeden egzemplarz każdej z nich,
 * a stan zakładki (np. pozycja przewinięcia) przetrwał przełączenie.
 */
private fun NavHostController.navigateToTopLevel(destination: TopLevelDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
