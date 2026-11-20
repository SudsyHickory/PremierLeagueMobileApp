package com.example.premierleagueapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Kolory specyficzne dla aplikacji, których nie ma w schemacie Material 3. */
@Immutable
data class LeagueColors(
    val zoneChampionsLeague: Color,
    val zoneEuropaLeague: Color,
    val zoneRelegation: Color
)

internal val LightLeagueColors = LeagueColors(
    zoneChampionsLeague = LightZoneChampionsLeague,
    zoneEuropaLeague = LightZoneEuropaLeague,
    zoneRelegation = LightZoneRelegation
)

internal val DarkLeagueColors = LeagueColors(
    zoneChampionsLeague = DarkZoneChampionsLeague,
    zoneEuropaLeague = DarkZoneEuropaLeague,
    zoneRelegation = DarkZoneRelegation
)

internal val LocalLeagueColors = staticCompositionLocalOf { LightLeagueColors }

/** Dostęp do [LeagueColors] bieżącego motywu, analogicznie do `MaterialTheme.colorScheme`. */
object LeagueTheme {
    val colors: LeagueColors
        @Composable
        @ReadOnlyComposable
        get() = LocalLeagueColors.current
}
