package com.playlistmaker.domain.repositories

import com.playlistmaker.domain.models.Music

interface SearchHistoryRepository {
    fun saveSearchHistory(trackList: List<Music>)
    fun getSearchHistory(): List<Music>
    fun clearSearchHistory()
}