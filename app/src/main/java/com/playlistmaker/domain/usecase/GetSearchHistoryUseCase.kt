package com.playlistmaker.domain.usecase

import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.repositories.SearchHistoryRepository


class GetSearchHistoryUseCase(private val repository: SearchHistoryRepository) {
    fun execute(): List<Music> {
        return repository.getSearchHistory()
    }
}