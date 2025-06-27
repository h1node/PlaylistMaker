package com.playlistmaker.domain.usecase

import com.playlistmaker.data.db.entity.MusicEntity
import com.playlistmaker.domain.repositories.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class GetAllFavoritesUseCase(
    private val repository: FavoritesRepository
) {
    fun execute(): Flow<List<MusicEntity>> {
        return repository.getAllFavoriteTracks()
            .map { tracks ->
                tracks.sortedByDescending { it.trackId }   // последние добавленные будут сверху
            }
    }
}