package com.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "music_table")
data class MusicEntity(
    @PrimaryKey val trackId: Long,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Int?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val primaryGenreName: String?,
    val releaseDate: Date?,
    val country: String?,
    val previewUrl: String?,
    val addedAt: Long = System.currentTimeMillis()
)
