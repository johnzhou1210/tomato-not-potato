package com.johnzhou.tomatonotpotato.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Dot(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(16.dp)) {
        val radius = size.minDimension / 2
        drawCircle(
            color = color,
            radius = radius,
            center = Offset(size.width / 2, size.height / 2)
        )
    }
}

@Preview
@Composable
fun DotPreview() {
    Dot(color = Color.Red)
}