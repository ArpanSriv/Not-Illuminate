package com.arpan.musicplayer.model

import java.util.ArrayList

// Created on 12/19/2017

class Album(
        val albumName: String,
        val songs: ArrayList<Song>?,
        val artistName: String,
        val albumArtUri: String) {

    override fun toString(): String {
        return "Album Details: $albumName, Songs: ${songs!!.size}"
    }
}