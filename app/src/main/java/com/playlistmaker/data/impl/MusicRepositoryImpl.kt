package com.playlistmaker.data.impl

import com.playlistmaker.data.api.MusicApi
import com.playlistmaker.data.db.AppDatabase
import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.repositories.MusicSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class MusicRepositoryImpl(private val api: MusicApi, private val database: AppDatabase) :
    MusicSearchRepository {
    override suspend fun searchMusic(query: String): Flow<List<Music>> = flow {
        try {
            val response = api.getMusic(query)
            val tracks = response.results.filter {
                it.trackName != null && (it.trackTimeMillis ?: 0) > 0
            }
            val favoriteIdSet = database.musicDao().getAllFavoriteTrackId().toSet()
            val tracksWithFavorites = tracks.map { track ->
                track.apply {
                    isFavorite = track.trackId in favoriteIdSet
                }
            }
            emit(tracksWithFavorites)
        } catch (e: Exception) {
            throw e
        }
    }
}