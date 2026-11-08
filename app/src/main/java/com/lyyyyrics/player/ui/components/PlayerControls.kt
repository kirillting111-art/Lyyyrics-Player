package com.lyyyyrics.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyyyyrics.player.player.PlayerState

@Composable
fun PlayerControls(state: PlayerState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("⏮", color = Color.White, fontSize = 28.sp)

        Spacer(Modifier.width(35.dp))

        Box(
            modifier = Modifier
                .size(65.dp)
                .background(Color.White.copy(alpha = 0.12f), CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                .clickable {
                    if (state.isPlaying) state.pause() else state.play()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (state.isPlaying) "Ⅱ" else "▶",
                color = Color.White,
                fontSize = 25.sp
            )
        }

        Spacer(Modifier.width(35.dp))

        Text("⏭", color = Color.White, fontSize = 28.sp)
    }
}
