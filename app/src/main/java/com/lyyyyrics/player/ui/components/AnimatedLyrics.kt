package com.lyyyyrics.player.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedLyrics(
    text: String
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = text,
            transitionSpec = {
                (fadeIn(tween(450)) + scaleIn(
                    initialScale = 0.96f,
                    animationSpec = tween(450)
                )) togetherWith fadeOut(tween(250))
            },
            label = "lyrics_transition"
        ) { currentText ->
            Text(
                text = currentText,
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
