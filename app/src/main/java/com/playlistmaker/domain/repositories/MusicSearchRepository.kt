package com.playlistmaker.domain.repositories

import com.playlistmaker.domain.models.Music


interface MusicSearchRepository {
    fun searchMusic(
        query: String,
        callback: (List<Music>) -> Unit,
        errorCallback: (Throwable) -> Unit
    )
}