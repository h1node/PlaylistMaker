package com.playlistmaker.domain.repositories

import com.playlistmaker.domain.models.Music


interface MusicRepository {
    fun searchMusic(
        query: String,
        callback: (List<Music>) -> Unit,
        errorCallback: (Throwable) -> Unit
    )

    fun saveSearchHistory(trackList: List<Music>)
    fun getSearchHistory(): List<Music>
    fun clearSearchHistory()
}