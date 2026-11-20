package com.example.premierleagueapp.ui.standings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.premierleagueapp.R
import com.example.premierleagueapp.domain.model.RefreshException
import com.example.premierleagueapp.domain.model.StandingType
import com.example.premierleagueapp.ui.components.FullScreenError
import com.example.premierleagueapp.ui.components.LastUpdatedChip
import com.example.premierleagueapp.ui.components.LeagueTopBar
import com.example.premierleagueapp.ui.components.ObserveAsEvents
import com.example.premierleagueapp.ui.components.refreshFailedRes
import com.example.premierleagueapp.ui.theme.PremierLeagueAppTheme
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Zakładka Tabela – część stanowa: łączy ekran z [StandingsViewModel],
 * zbiera stan i zamienia zdarzenia na snackbar „Ponów”.
 */
@Composable
fun StandingsRoute(
    modifier: Modifier = Modifier,
    viewModel: StandingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is StandingsEvent.RefreshFailed -> {
                val result = snackbarHostState.showSnackbar(
                    message = resources.getString(event.reason.refreshFailedRes()),
                    actionLabel = resources.getString(R.string.action_retry_short),
                    duration = SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed) viewModel.onRefresh()
            }
        }
    }

    StandingsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onRefresh = viewModel::onRefresh,
        onRetry = viewModel::onRetry,
        onTypeSelected = viewModel::onTypeSelected,
        modifier = modifier
    )
}

/** Zakładka Tabela – część bezstanowa: rysuje [uiState] i przekazuje akcje w górę. */
@Composable
fun StandingsScreen(
    uiState: StandingsUiState,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onTypeSelected: (StandingType) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            LeagueTopBar(
                subtitle = subtitle(uiState),
                actions = {
                    IconButton(
                        onClick = if (uiState is StandingsUiState.Error) onRetry else onRefresh,
                        enabled = uiState !is StandingsUiState.Loading &&
                            !(uiState is StandingsUiState.Content && uiState.isRefreshing)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_refresh),
                            contentDescription = stringResource(R.string.action_refresh)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        when (uiState) {
            StandingsUiState.Loading -> StandingsLoading(contentModifier)
            is StandingsUiState.Error -> FullScreenError(
                reason = uiState.reason,
                onRetry = onRetry,
                modifier = contentModifier
            )
            is StandingsUiState.Content -> StandingsContent(
                content = uiState,
                onRefresh = onRefresh,
                onTypeSelected = onTypeSelected,
                modifier = contentModifier
            )
        }
    }
}

@Composable
private fun subtitle(uiState: StandingsUiState): String {
    val season = (uiState as? StandingsUiState.Content)?.season
    return if (season != null) {
        stringResource(R.string.standings_subtitle_season, season.startYear, season.endYear % 100)
    } else {
        stringResource(R.string.standings_subtitle)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StandingsContent(
    content: StandingsUiState.Content,
    onRefresh: () -> Unit,
    onTypeSelected: (StandingType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        StandingTypeSelector(
            selected = content.type,
            onTypeSelected = onTypeSelected,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        PullToRefreshBox(
            isRefreshing = content.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(key = "updated") {
                    LastUpdatedChip(
                        lastUpdated = content.lastUpdated,
                        isRefreshing = content.isRefreshing
                    )
                }
                // 20 wierszy – cała tabela jako jedna karta, bez potrzeby leniwego ładowania wierszy.
                item(key = "table") { StandingsTable(rows = content.rows) }
                item(key = "legend") { ZoneLegend(Modifier.padding(horizontal = 4.dp)) }
            }
        }
    }
}

@Composable
private fun StandingTypeSelector(
    selected: StandingType,
    onTypeSelected: (StandingType) -> Unit,
    modifier: Modifier = Modifier
) {
    val types = StandingType.entries
    SingleChoiceSegmentedButtonRow(modifier.fillMaxWidth()) {
        types.forEachIndexed { index, type ->
            SegmentedButton(
                selected = type == selected,
                onClick = { onTypeSelected(type) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = types.size),
                label = { Text(stringResource(type.labelRes()), maxLines = 1) }
            )
        }
    }
}

@Composable
private fun StandingsLoading(modifier: Modifier = Modifier) {
    Column(modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.loading_data),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        StandingsTableSkeleton()
    }
}

private fun StandingType.labelRes(): Int = when (this) {
    StandingType.TOTAL -> R.string.standings_type_total
    StandingType.HOME -> R.string.standings_type_home
    StandingType.AWAY -> R.string.standings_type_away
}

@Preview(showBackground = true, heightDp = 640)
@Composable
private fun StandingsContentPreview() {
    PremierLeagueAppTheme {
        StandingsScreen(
            uiState = StandingsUiState.Content(
                rows = PreviewStandingRows,
                type = StandingType.TOTAL,
                isRefreshing = false,
                lastUpdated = Instant.now().minus(4, ChronoUnit.MINUTES),
                season = Season(2026, 2027)
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onRefresh = {},
            onRetry = {},
            onTypeSelected = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 640)
@Composable
private fun StandingsRefreshingPreview() {
    PremierLeagueAppTheme {
        StandingsScreen(
            uiState = StandingsUiState.Content(
                rows = PreviewStandingRows,
                type = StandingType.HOME,
                isRefreshing = true,
                lastUpdated = Instant.now(),
                season = null
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onRefresh = {},
            onRetry = {},
            onTypeSelected = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 640)
@Composable
private fun StandingsLoadingPreview() {
    PremierLeagueAppTheme {
        StandingsScreen(
            uiState = StandingsUiState.Loading,
            snackbarHostState = remember { SnackbarHostState() },
            onRefresh = {},
            onRetry = {},
            onTypeSelected = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 640)
@Composable
private fun StandingsErrorPreview() {
    PremierLeagueAppTheme {
        StandingsScreen(
            uiState = StandingsUiState.Error(RefreshException.Reason.NO_CONNECTION),
            snackbarHostState = remember { SnackbarHostState() },
            onRefresh = {},
            onRetry = {},
            onTypeSelected = {}
        )
    }
}
