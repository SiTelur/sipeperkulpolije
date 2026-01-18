package com.polije.sipeperpolije.utils

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
private fun Modifier.shimmer(
    cornerRadius : Dp = 0.dp,
) : Modifier {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.3f)
    )

    val transition = rememberInfiniteTransition()
    val translateAnimation by transition.animateFloat(-400f, 1200f, infiniteRepeatable(animation = tween(durationMillis = 1200)), label = "Translate")

    return this.drawWithCache {
        val brush = Brush.linearGradient(
            shimmerColors,
            start = Offset(translateAnimation, 0f)
            , end = Offset(translateAnimation + size.width / 1.5f, size.height)
        )

        val cornerPx =cornerRadius.toPx()

        onDrawWithContent {
            onDrawWithContent {
                drawRoundRect(
                    brush = brush,
                    cornerRadius = CornerRadius(cornerPx, cornerPx),
                    size = size
                )
            }
        }
    }
}

@Composable
fun Modifier.shimmer(corderRadius: Dp = 0.dp, isLoading : Boolean) : Modifier {
    return if (isLoading) this.shimmer(corderRadius) else this
}



