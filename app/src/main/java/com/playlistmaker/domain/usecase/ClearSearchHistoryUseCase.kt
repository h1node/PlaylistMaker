package com.playlistmaker.domain.usecase

import com.playlistmaker.domain.repositories.MusicRepository

class ClearSearchHistoryUseCase(private val repository: MusicRepository) {
    fun execute() {
        repository.clearSearchHistory()
    }
}