package com.lyyyyrics.player.player

import android.content.Context

class SongRepository(
    private val context: Context
) {
    fun getSongs(): List<Song> {
        return SongLoader.loadSongs(context)
    }
}
