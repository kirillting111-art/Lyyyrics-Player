package com.lyyyyrics.player.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomWidgets() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlassWidget(
            modifier = Modifier.weight(1f),
            symbol = "♡",
            title = "EMOTION",
            delay = 0
        )

        GlassWidget(
            modifier = Modifier.weight(1f),
            symbol = "✧",
            title = "ENERGY",
            delay = 400
        )

        GlassWidget(
            modifier = Modifier.weight(1f),
            symbol = "◌",
            title = "ATMOSPHERE",
            delay = 800
        )
    }
}

@Composable
private fun GlassWidget(
    modifier: Modifier,
    symbol: String,
    title: String,
    delay: Int
) {
    val infinite = rememberInfiniteTransition(label = "widget")

    val scale by infinite.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = delay),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = modifier
            .height(105.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(23.dp))
            .background(Color.White.copy(alpha = 0.07f))
            .border(
                1.dp,
                Color.White.copy(alpha = 0.13f),
                RoundedCornerShape(23.dp)
            )
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = symbol,
            color = Color.White,
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = title,
            color = Color.White.copy(alpha = 0.55f),
            fontSize = 8.sp,
            letterSpacing = 1.sp
        )
    }
}
