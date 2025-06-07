package com.playlistmaker.domain.usecase

import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.repositories.MusicSearchRepository
import kotlinx.coroutines.flow.Flow


class SearchMusicUseCase(private val repository: MusicSearchRepository) {
    suspend fun execute(query: String): Flow<List<Music>> {
        return repository.searchMusic(query)
    }
}