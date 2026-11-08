package com.lyyyyrics.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyyyyrics.player.player.rememberPlayerState
import com.lyyyyrics.player.ui.components.BottomWidgets

@Composable
fun PlayerScreen() {

    val playerState = rememberPlayerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF25243B),
                        Color(0xFF10111A),
                        Color(0xFF07080C)
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            "LYYYRICS PLAYER",
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 13.sp,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(55.dp))

        Text(
            "Весь этот мир",
            color = Color.White,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "NEMIGA",
            color = Color.White.copy(alpha = 0.55f),
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            "И весь этот мир\nстановится другим...",
            color = Color.White,
            fontSize = 25.sp,
            lineHeight = 37.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.weight(1f))

        BottomWidgets(position = playerState.position)
    }
}
