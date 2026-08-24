package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.VoidDark

@Composable
fun HoloBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "holoGrid")
    val gridOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gridOffset"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Deep space background fill
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    VoidDark,
                    Color(0xFF030A18),
                    Color(0xFF01040A)
                )
            )
        )

        // 2. Center radial glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    CyanPrimary.copy(alpha = 0.12f),
                    PurplePrimary.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = Offset(width / 2f, height / 2f),
                radius = width * 0.75f
            ),
            radius = width * 0.75f,
            center = Offset(width / 2f, height / 2f)
        )

        // 3. Cyber grid lines
        val gridSize = 40f
        val startY = (gridOffset % gridSize)

        // Horizontal cyber lines
        var y = startY
        while (y < height) {
            val alpha = (0.04f + 0.03f * (y / height)).coerceIn(0.02f, 0.08f)
            drawLine(
                color = CyanPrimary.copy(alpha = alpha),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += gridSize
        }

        // Vertical cyber lines
        var x = 0f
        while (x < width) {
            val distCenter = kotlin.math.abs(x - width / 2f) / (width / 2f)
            val alpha = (0.06f - 0.03f * distCenter).coerceIn(0.02f, 0.07f)
            drawLine(
                color = CyanPrimary.copy(alpha = alpha),
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            x += gridSize
        }
    }
}
