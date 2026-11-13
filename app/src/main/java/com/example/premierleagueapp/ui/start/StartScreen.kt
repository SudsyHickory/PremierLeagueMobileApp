package com.example.premierleagueapp.ui.start

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.premierleagueapp.R
import com.example.premierleagueapp.ui.components.PlaceholderScreen
import com.example.premierleagueapp.ui.theme.PremierLeagueAppTheme

/** Zakładka Start – podsumowanie ligi. Właściwa treść powstanie w etapie 5. */
@Composable
fun StartScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        subtitle = stringResource(R.string.start_subtitle),
        title = stringResource(R.string.tab_start),
        message = stringResource(R.string.placeholder_start),
        iconRes = R.drawable.ic_tab_start,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun StartScreenPreview() {
    PremierLeagueAppTheme {
        StartScreen()
    }
}
