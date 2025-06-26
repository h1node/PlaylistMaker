package com.playlistmaker.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date


@Parcelize
data class Music(
    val trackId: Long,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Int?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: Date?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    var isFavorite: Boolean = false
) : Parcelable
