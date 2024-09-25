package com.playlistmaker.data.itunesdb

data class Music(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String
)
