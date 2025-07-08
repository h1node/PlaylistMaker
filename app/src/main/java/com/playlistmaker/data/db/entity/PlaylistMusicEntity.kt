package com.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "playlist_music_table",
    primaryKeys = ["playlistId", "trackId"],
    indices = [Index(value = ["playlistId"])]
)
data class PlaylistMusicEntity(
    val trackId: Long,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Int?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val primaryGenreName: String?,
    val releaseDate: Date?,
    val country: String?,
    val previewUrl: String?,
    val playlistId: Long
)