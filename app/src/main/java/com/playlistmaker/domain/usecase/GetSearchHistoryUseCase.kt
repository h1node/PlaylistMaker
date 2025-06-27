package com.playlistmaker.domain.usecase

import com.playlistmaker.data.db.AppDatabase
import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.repositories.SearchHistoryRepository


class GetSearchHistoryUseCase(
    private val repository: SearchHistoryRepository,
    private val database: AppDatabase
) {
    suspend fun execute(): List<Music> {
        val history = repository.getSearchHistory()

        val favoriteIdSet = database.musicDao().getAllFavoriteTrackId().toSet()
        return history.map { track ->
            track.apply {
                isFavorite = track.trackId in favoriteIdSet
            }
        }
    }
}