package com.arpan.musicplayer.model

import android.net.Uri

class Song(val songName: String,
           val albumName: String,
           val albumId: Long,
           val albumArtUri: Uri,
           val artistName: String,
           val uri: String,
           val duration: Int) {

    override fun toString(): String {
        return "Song Details: $songName $artistName $albumName"
    }
}
