package com.lyyyyrics.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlayerScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF25243B),
                        Color(0xFF10111A),
                        Color(0xFF07080C)
                    )
                )
            )
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(35.dp))

            Text(
                text = "LYYYRICS PLAYER",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 13.sp,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(55.dp))

            Text(
                text = "Весь этот мир",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "NEMIGA",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 15.sp,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "И весь этот мир\nстановится другим...",
                color = Color.White,
                fontSize = 25.sp,
                lineHeight = 37.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "WIDGETS",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(85.dp)
                            .background(
                                Color.White.copy(alpha = 0.09f),
                                RoundedCornerShape(22.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✦",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 27.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}
