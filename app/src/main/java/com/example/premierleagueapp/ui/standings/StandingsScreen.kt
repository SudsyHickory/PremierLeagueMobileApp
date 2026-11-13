package com.example.premierleagueapp.ui.standings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.premierleagueapp.R
import com.example.premierleagueapp.ui.components.PlaceholderScreen
import com.example.premierleagueapp.ui.theme.PremierLeagueAppTheme

/** Zakładka Tabela. Właściwa treść powstanie w etapie 3. */
@Composable
fun StandingsScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        subtitle = stringResource(R.string.standings_subtitle),
        title = stringResource(R.string.tab_standings),
        message = stringResource(R.string.placeholder_standings),
        iconRes = R.drawable.ic_tab_standings,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun StandingsScreenPreview() {
    PremierLeagueAppTheme {
        StandingsScreen()
    }
}
