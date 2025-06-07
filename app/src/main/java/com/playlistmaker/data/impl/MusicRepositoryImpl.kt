package com.playlistmaker.data.impl

import com.playlistmaker.data.api.MusicApi
import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.repositories.MusicSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class MusicRepositoryImpl(private val api: MusicApi) : MusicSearchRepository {
    override suspend fun searchMusic(query: String): Flow<List<Music>> = flow {
        try {
            val response = api.getMusic(query)
            val tracks = response.results.filter {
                it.trackName != null && (it.trackTimeMillis ?: 0) > 0
            }
            emit(tracks)
        } catch (e: Exception) {
            throw e
        }
    }
}