package com.lyyyyrics.player.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyyyyrics.player.widgets.WidgetEngine

@Composable
fun BottomWidgets(
    position: Long = 0L
) {
    val scene = remember(position) {
        WidgetEngine().getWidgets(position)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        scene.symbols.forEachIndexed { index, symbol ->
            AnimatedContent(
                targetState = symbol,
                transitionSpec = {
                    fadeIn(tween(500)) togetherWith fadeOut(tween(300))
                },
                label = "widget_transition",
                modifier = Modifier.weight(1f)
            ) { currentSymbol ->
                GlassWidget(
                    symbol = currentSymbol,
                    title = scene.captions[index],
                    accent = Color(scene.accentColor),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun GlassWidget(
    symbol: String,
    title: String,
    accent: Color,
    modifier: Modifier
) {
    val infinite = rememberInfiniteTransition(label = "widget")

    val scale by infinite.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            tween(1800),
            RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = modifier
            .height(105.dp)
            .scale(scale)
            .clip(RoundedCornerShape(23.dp))
            .background(accent.copy(alpha = 0.10f))
            .border(
                1.dp,
                accent.copy(alpha = 0.28f),
                RoundedCornerShape(23.dp)
            )
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = symbol,
            color = accent,
            fontSize = 29.sp
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = title,
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 8.sp,
            letterSpacing = 1.sp
        )
    }
}
