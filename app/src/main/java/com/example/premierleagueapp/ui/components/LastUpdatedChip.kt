package com.example.premierleagueapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.premierleagueapp.R
import com.example.premierleagueapp.ui.theme.PremierLeagueAppTheme
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Chip z wiekiem danych: „Zaktualizowano X min temu” albo „Odświeżanie…”.
 * Tekst przelicza się co minutę, więc nie „zastyga” na otwartym ekranie.
 * Gdy danych nie ma ([lastUpdated] = `null`) i nic się nie odświeża, chip się nie wyświetla.
 */
@Composable
fun LastUpdatedChip(
    lastUpdated: Instant?,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    var now by remember { mutableStateOf(Instant.now()) }
    LaunchedEffect(lastUpdated) {
        while (true) {
            now = Instant.now()
            delay(TICK_MILLIS)
        }
    }

    val text = when {
        isRefreshing -> stringResource(R.string.refreshing)
        lastUpdated != null -> updatedAgoText(updatedAgo(lastUpdated, now))
        else -> return
    }

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_refresh),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(text = text, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun updatedAgoText(age: UpdatedAgo): String = when (age) {
    UpdatedAgo.JustNow -> stringResource(R.string.updated_just_now)
    is UpdatedAgo.Minutes -> stringResource(R.string.updated_minutes_ago, age.value)
    is UpdatedAgo.Hours -> stringResource(R.string.updated_hours_ago, age.value)
    is UpdatedAgo.Days -> pluralStringResource(R.plurals.updated_days_ago, age.value, age.value)
}

private const val TICK_MILLIS = 60_000L

@Preview(showBackground = true)
@Composable
private fun LastUpdatedChipPreview() {
    PremierLeagueAppTheme {
        LastUpdatedChip(
            lastUpdated = Instant.now().minus(4, ChronoUnit.MINUTES),
            isRefreshing = false
        )
    }
}
