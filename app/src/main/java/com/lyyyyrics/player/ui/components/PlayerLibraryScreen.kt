package com.lyyyyrics.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyyyyrics.player.player.*

@Composable
fun PlayerLibraryScreen(
    onSongSelected: (Song) -> Unit
) {
    val context = LocalContext.current
    val viewModel = rememberPlayerViewModel()

    rememberMusicPermission {
        viewModel.loadSongs(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF08090D))
            .padding(20.dp)
    ) {
        Text(
            text = "МОЯ МУЗЫКА",
            color = Color.White,
            fontSize = 24.sp
        )

        Spacer(Modifier.height(20.dp))

        when {
            viewModel.isLoading -> {
                CircularProgressIndicator(
                    color = Color(0xFFB99AFF)
                )
            }

            viewModel.songs.isEmpty() -> {
                Text(
                    text = viewModel.error ?: "Загрузка музыки...",
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            else -> {
                SongLibrary(
                    songs = viewModel.songs,
                    onSongSelected = onSongSelected
                )
            }
        }
    }
}
