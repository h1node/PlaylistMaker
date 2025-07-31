package com.playlistmaker.domain.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Playlist(
    val id: Long,
    val name: String,
    val description: String?,
    val coverUri: String?,
    val trackCount: Int = 0
) : Parcelable