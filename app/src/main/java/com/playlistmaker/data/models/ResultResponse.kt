package com.playlistmaker.data.models

import com.playlistmaker.domain.models.Music


data class ResultResponse(
    val resultCount: Int,
    val results: List<Music>
)
