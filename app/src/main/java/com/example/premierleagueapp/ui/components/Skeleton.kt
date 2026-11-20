package com.example.premierleagueapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Szary prostokąt-zaślepka do szkieletów (skeleton) przy pierwszym ładowaniu.
 * Rozmiar i kształt ustala wywołujący przez [modifier] i [shape].
 */
@Composable
fun SkeletonBlock(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp)
) {
    Box(modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest, shape))
}
