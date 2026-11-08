package com.lyyyyrics.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyyyyrics.player.player.rememberMusicPermission
import com.lyyyyrics.player.player.rememberPlayerViewModel
import com.lyyyyrics.player.ui.components.BottomWidgets
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen() {
    val context = LocalContext.current
    val viewModel = rememberPlayerViewModel()
    val audio = viewModel.audioState

    rememberMusicPermission {
        viewModel.loadSongs(context)
    }

    LaunchedEffect(audio.isPlaying) {
        while (audio.isPlaying) {
            audio.updatePosition()
            delay(250)
        }
    }

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
            .padding(horizontal = 22.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = "LYYYRICS PLAYER",
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 12.sp,
            letterSpacing = 4.sp
        )

        Spacer(Modifier.height(28.dp))

        Text(
            text = audio.currentSong?.title ?: "Выбери свою музыку",
            color = Color.White,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = audio.currentSong?.artist ?: "Музыка с твоего устройства",
            color = Color.White.copy(alpha = 0.55f),
            fontSize = 14.sp
        )

        Spacer(Modifier.height(22.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    Color.White.copy(alpha = 0.035f),
                    RoundedCornerShape(26.dp)
                )
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (audio.currentSong == null) {
                    "Выбери трек ниже"
                } else {
                    "Текст песни появится здесь"
                },
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(18.dp))

        Slider(
            value = audio.position.toFloat().coerceIn(
                0f,
                audio.duration.toFloat().coerceAtLeast(1f)
            ),
            onValueChange = { audio.seekTo(it.toLong()) },
            valueRange = 0f..audio.duration.toFloat().coerceAtLeast(1f),
            enabled = audio.currentSong != null && audio.duration > 0L
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(audio.position), color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
            Text(formatTime(audio.duration), color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
        }

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(42.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⏮",
                color = Color.White,
                fontSize = 30.sp,
                modifier = Modifier.clickable { viewModel.previousSong() }
            )
            Text(
                text = if (audio.isPlaying) "Ⅱ" else "▶",
                color = Color.White,
                fontSize = 38.sp,
                modifier = Modifier.clickable { audio.togglePlayback() }
            )
            Text(
                text = "⏭",
                color = Color.White,
                fontSize = 30.sp,
                modifier = Modifier.clickable { viewModel.nextSong() }
            )
        }

        Spacer(Modifier.height(20.dp))
        BottomWidgets(position = audio.position)
        Spacer(Modifier.height(16.dp))

        Text(
            text = when {
                audio.error != null -> audio.error!!
                viewModel.isLoading -> "Загружаю музыку…"
                viewModel.songs.isEmpty() -> "Музыка не найдена или нет доступа"
                else -> "ТВОЯ МУЗЫКА"
            },
            color = Color.White.copy(alpha = 0.55f),
            fontSize = 11.sp,
            letterSpacing = 1.5.sp
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 145.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(viewModel.songs, key = { it.uri.toString() }) { song ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (audio.currentSong?.uri == song.uri) {
                                Color.White.copy(alpha = 0.13f)
                            } else {
                                Color.White.copy(alpha = 0.045f)
                            },
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { viewModel.selectSong(song) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = song.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = (milliseconds.coerceAtLeast(0L) / 1000L)
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%d:%02d".format(minutes, seconds)
}
