package com.lyyyyrics.player.player

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

data class Song(
    val uri: Uri,
    val title: String,
    val artist: String,
    val duration: Long
)

object SongLoader {

    fun loadSongs(context: Context): List<Song> {
        val songs = mutableListOf<Song>()

        val collection = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            android.provider.MediaStore.Audio.Media._ID,
            android.provider.MediaStore.Audio.Media.TITLE,
            android.provider.MediaStore.Audio.Media.ARTIST,
            android.provider.MediaStore.Audio.Media.DURATION
        )

        val selection =
            "${android.provider.MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            "${android.provider.MediaStore.Audio.Media.TITLE} ASC"
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow(
                android.provider.MediaStore.Audio.Media._ID
            )

            val titleColumn = cursor.getColumnIndexOrThrow(
                android.provider.MediaStore.Audio.Media.TITLE
            )

            val artistColumn = cursor.getColumnIndexOrThrow(
                android.provider.MediaStore.Audio.Media.ARTIST
            )

            val durationColumn = cursor.getColumnIndexOrThrow(
                android.provider.MediaStore.Audio.Media.DURATION
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)

                val title = cursor.getString(titleColumn) ?: "Без названия"
                val artist = cursor.getString(artistColumn) ?: "Неизвестный исполнитель"
                val duration = cursor.getLong(durationColumn)

                val uri = Uri.withAppendedPath(collection, id.toString())

                songs.add(
                    Song(
                        uri = uri,
                        title = title,
                        artist = artist,
                        duration = duration
                    )
                )
            }
        }

        return songs
    }
}
