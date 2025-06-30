package com.playlistmaker.data.db

import com.playlistmaker.data.db.entity.MusicEntity
import com.playlistmaker.domain.models.Music


fun Music.toEntity(): MusicEntity = MusicEntity(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    primaryGenreName = primaryGenreName,
    releaseDate = releaseDate,
    country = country,
    previewUrl = previewUrl
)

fun MusicEntity.toMusic(): Music = Music(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    isFavorite = true
)