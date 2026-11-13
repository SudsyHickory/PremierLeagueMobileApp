package com.example.premierleagueapp.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.premierleagueapp.R

/** Zakładki dolnego paska nawigacji, w kolejności wyświetlania. */
enum class TopLevelDestination(
    val route: String,
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val iconRes: Int
) {
    START("start", R.string.tab_start, R.drawable.ic_tab_start),
    STANDINGS("standings", R.string.tab_standings, R.drawable.ic_tab_standings),
    MATCHES("matches", R.string.tab_matches, R.drawable.ic_tab_matches),
    MEMORIES("memories", R.string.tab_memories, R.drawable.ic_tab_memories)
}
