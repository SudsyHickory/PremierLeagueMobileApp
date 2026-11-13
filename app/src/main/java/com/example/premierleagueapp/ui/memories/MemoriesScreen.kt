package com.example.premierleagueapp.ui.memories

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.premierleagueapp.R
import com.example.premierleagueapp.ui.components.PlaceholderScreen
import com.example.premierleagueapp.ui.theme.PremierLeagueAppTheme

/** Zakładka Kibic – dziennik wspomnień. Właściwa treść powstanie w etapie 7a. */
@Composable
fun MemoriesScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        subtitle = stringResource(R.string.memories_subtitle),
        title = stringResource(R.string.tab_memories),
        message = stringResource(R.string.placeholder_memories),
        iconRes = R.drawable.ic_tab_memories,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun MemoriesScreenPreview() {
    PremierLeagueAppTheme {
        MemoriesScreen()
    }
}
