package com.playlistmaker.domain.usecase

import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.repositories.MusicRepository

class GetSearchHistoryUseCase(private val repository: MusicRepository) {
    fun execute(): List<Music> {
        return repository.getSearchHistory()
    }
}