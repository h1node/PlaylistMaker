package com.playlistmaker.domain.usecase

import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.repositories.MusicRepository

class ManageSearchHistoryUseCase(private val repository: MusicRepository) {
    fun addTrackToHistory(track: Music) {
        val history = repository.getSearchHistory().toMutableList()
        history.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }
        history.add(0, track)
        if (history.size > 10) history.removeAt(10)
        repository.saveSearchHistory(history)
    }
}