package com.lyyyyrics.player.player

class PlaylistManager {

    private val playlist = mutableListOf<Song>()

    var currentIndex: Int = -1
        private set

    fun setPlaylist(songs: List<Song>, selectedSong: Song) {
        playlist.clear()
        playlist.addAll(songs)
        currentIndex = playlist.indexOf(selectedSong)
    }

    fun next(): Song? {
        if (playlist.isEmpty()) return null

        currentIndex = (currentIndex + 1) % playlist.size
        return playlist[currentIndex]
    }

    fun previous(): Song? {
        if (playlist.isEmpty()) return null

        currentIndex = if (currentIndex <= 0) {
            playlist.lastIndex
        } else {
            currentIndex - 1
        }

        return playlist[currentIndex]
    }

    fun current(): Song? {
        return playlist.getOrNull(currentIndex)
    }

    fun isEmpty(): Boolean {
        return playlist.isEmpty()
    }
}
