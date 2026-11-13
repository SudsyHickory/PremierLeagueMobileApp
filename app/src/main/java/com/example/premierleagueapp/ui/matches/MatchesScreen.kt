package com.example.premierleagueapp.ui.matches

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.premierleagueapp.R
import com.example.premierleagueapp.ui.components.PlaceholderScreen
import com.example.premierleagueapp.ui.theme.PremierLeagueAppTheme

/** Zakładka Mecze. Właściwa treść powstanie w etapie 4. */
@Composable
fun MatchesScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        subtitle = stringResource(R.string.matches_subtitle),
        title = stringResource(R.string.tab_matches),
        message = stringResource(R.string.placeholder_matches),
        iconRes = R.drawable.ic_tab_matches,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun MatchesScreenPreview() {
    PremierLeagueAppTheme {
        MatchesScreen()
    }
}
