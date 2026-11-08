package com.lyyyyrics.player.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyyyyrics.player.lyrics.LyricLine

@Composable
fun LyricsView(
    lines: List<LyricLine>,
    currentIndex: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        lines.forEachIndexed { index, line ->

            val isCurrent = index == currentIndex

            val color by animateColorAsState(
                targetValue = if (isCurrent) {
                    Color.White
                } else {
                    Color.White.copy(alpha = 0.28f)
                },
                label = "lyric_color"
            )

            Text(
                text = line.text,
                color = color,
                fontSize = if (isCurrent) 25.sp else 19.sp,
                fontWeight = if (isCurrent) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
            )
        }
    }
}
