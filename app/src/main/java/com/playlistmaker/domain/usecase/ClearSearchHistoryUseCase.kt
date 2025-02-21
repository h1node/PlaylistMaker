package com.playlistmaker.domain.usecase

import com.playlistmaker.domain.repositories.SearchHistoryRepository

class ClearSearchHistoryUseCase(private val repository: SearchHistoryRepository) {
    fun execute() {
        repository.clearSearchHistory()
    }
}