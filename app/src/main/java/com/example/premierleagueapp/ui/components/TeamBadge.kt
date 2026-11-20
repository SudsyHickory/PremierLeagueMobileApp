package com.example.premierleagueapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.premierleagueapp.domain.model.Team

/**
 * Znaczek drużyny: kółko ze skrótem TLA, jak na makietach.
 * Herb z [Team.crestUrl] (Coil) dojdzie razem z prawdziwymi danymi w etapie 8 – dane mock herbów nie mają.
 */
@Composable
fun TeamBadge(
    team: Team,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
    ) {
        Text(
            text = team.tla,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}
