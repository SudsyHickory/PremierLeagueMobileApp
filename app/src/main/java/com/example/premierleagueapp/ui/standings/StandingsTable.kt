package com.example.premierleagueapp.ui.standings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.premierleagueapp.R
import com.example.premierleagueapp.domain.model.StandingRow
import com.example.premierleagueapp.domain.model.Team
import com.example.premierleagueapp.ui.components.SkeletonBlock
import com.example.premierleagueapp.ui.components.TeamBadge
import com.example.premierleagueapp.ui.theme.LeagueTheme
import com.example.premierleagueapp.ui.theme.PremierLeagueAppTheme

/** Szerokości kolumn wspólne dla nagłówka, wierszy i szkieletu – kolumny zawsze się pokrywają. */
private object Columns {
    val position = 28.dp
    val played = 32.dp
    val goalDifference = 40.dp
    val points = 36.dp
}

private val RowHeight = 44.dp

/** Karta z pełną tabelą: nagłówek kolumn i wiersze. Bezstanowa – dostaje dane w parametrach. */
@Composable
fun StandingsTable(
    rows: List<StandingRow>,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        StandingsHeaderRow()
        rows.forEach { row ->
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            StandingRowItem(row = row, zone = zoneFor(row.position, rows.size))
        }
    }
}

@Composable
fun StandingsHeaderRow(modifier: Modifier = Modifier) {
    val style = MaterialTheme.typography.labelSmall
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .padding(horizontal = 16.dp)
    ) {
        Text(stringResource(R.string.standings_column_position), style = style, color = color, modifier = Modifier.width(Columns.position))
        Text(stringResource(R.string.standings_column_team), style = style, color = color, modifier = Modifier.weight(1f))
        Text(stringResource(R.string.standings_column_played), style = style, color = color, textAlign = TextAlign.Center, modifier = Modifier.width(Columns.played))
        Text(stringResource(R.string.standings_column_goal_difference), style = style, color = color, textAlign = TextAlign.Center, modifier = Modifier.width(Columns.goalDifference))
        Text(stringResource(R.string.standings_column_points), style = style, color = color, textAlign = TextAlign.End, modifier = Modifier.width(Columns.points))
    }
}

/** Jeden wiersz tabeli; kolor numeru pozycji oznacza [zone]. */
@Composable
fun StandingRowItem(
    row: StandingRow,
    zone: StandingZone?,
    modifier: Modifier = Modifier
) {
    val style = MaterialTheme.typography.bodyMedium
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(RowHeight)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = row.position.toString(),
            style = style,
            fontWeight = FontWeight.SemiBold,
            color = zone?.color() ?: MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(Columns.position)
        )
        TeamBadge(team = row.team)
        Spacer(Modifier.width(12.dp))
        Text(
            text = row.team.shortName,
            style = style,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = row.played.toString(),
            style = style,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(Columns.played)
        )
        Text(
            text = formatGoalDifference(row.goalDifference),
            style = style,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(Columns.goalDifference)
        )
        Text(
            text = row.points.toString(),
            style = style,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.width(Columns.points)
        )
    }
}

/** Legenda kolorów stref pod tabelą. */
@Composable
fun ZoneLegend(modifier: Modifier = Modifier) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        StandingZone.entries.forEach { zone ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(10.dp)
                        .background(zone.color(), CircleShape)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(zone.labelRes()),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Szkielet tabeli na stan Loading – te same wymiary co prawdziwe wiersze. */
@Composable
fun StandingsTableSkeleton(
    modifier: Modifier = Modifier,
    rowCount: Int = 12
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column {
            repeat(rowCount) { index ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(RowHeight)
                        .padding(horizontal = 16.dp)
                ) {
                    SkeletonBlock(Modifier.size(width = 14.dp, height = 12.dp))
                    Spacer(Modifier.width(Columns.position - 14.dp))
                    SkeletonBlock(Modifier.size(28.dp), shape = CircleShape)
                    Spacer(Modifier.width(12.dp))
                    SkeletonBlock(Modifier.size(width = 110.dp, height = 12.dp))
                    Spacer(Modifier.weight(1f))
                    SkeletonBlock(Modifier.size(width = 72.dp, height = 12.dp))
                }
            }
        }
    }
}

/** Bilans bramek ze znakiem: „+12”, „0”, „-3”. */
internal fun formatGoalDifference(goalDifference: Int): String =
    if (goalDifference > 0) "+$goalDifference" else goalDifference.toString()

@Composable
private fun StandingZone.color(): Color = when (this) {
    StandingZone.CHAMPIONS_LEAGUE -> LeagueTheme.colors.zoneChampionsLeague
    StandingZone.EUROPA_LEAGUE -> LeagueTheme.colors.zoneEuropaLeague
    StandingZone.RELEGATION -> LeagueTheme.colors.zoneRelegation
}

private fun StandingZone.labelRes(): Int = when (this) {
    StandingZone.CHAMPIONS_LEAGUE -> R.string.zone_champions_league
    StandingZone.EUROPA_LEAGUE -> R.string.zone_europa_league
    StandingZone.RELEGATION -> R.string.zone_relegation
}

/** Kilka przykładowych wierszy do podglądów (dane mock są tylko w wariancie `mock`). */
internal val PreviewStandingRows: List<StandingRow> = listOf(
    Triple("Arsenal", "ARS", 20),
    Triple("Man City", "MCI", 19),
    Triple("Liverpool", "LIV", 17),
    Triple("Chelsea", "CHE", 15),
    Triple("Aston Villa", "AVL", 14),
    Triple("Newcastle", "NEW", 14)
).mapIndexed { index, (name, tla, points) ->
    StandingRow(
        position = index + 1,
        team = Team(id = index, name = name, shortName = name, tla = tla),
        played = 8,
        goalDifference = 12 - index * 2,
        points = points
    )
}

@Preview(showBackground = true)
@Composable
private fun StandingsTablePreview() {
    PremierLeagueAppTheme {
        Column(Modifier.padding(16.dp)) {
            StandingsTable(rows = PreviewStandingRows)
            Spacer(Modifier.height(12.dp))
            ZoneLegend()
        }
    }
}
