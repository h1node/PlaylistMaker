package com.playlistmaker.domain.usecase

import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.repositories.MusicSearchRepository

class SearchMusicUseCase(private val repository: MusicSearchRepository) {
    fun execute(
        query: String,
        callback: (List<Music>) -> Unit,
        errorCallback: (Throwable) -> Unit
    ) {
        repository.searchMusic(query, callback, errorCallback)
    }
}