package com.lyyyyrics.player.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyyyyrics.player.player.Song

@Composable
fun SongLibrary(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(songs) { song ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSongSelected(song)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.07f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "♫",
                        color = Color(0xFFB99AFF),
                        fontSize = 25.sp
                    )

                    Spacer(Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = song.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = song.artist,
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = formatDuration(song.duration),
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

private fun formatDuration(duration: Long): String {
    val minutes = duration / 1000 / 60
    val seconds = duration / 1000 % 60

    return "%d:%02d".format(minutes, seconds)
}
