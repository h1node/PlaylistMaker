package com.playlistmaker.domain.repositories

import com.playlistmaker.domain.models.Music
import kotlinx.coroutines.flow.Flow


interface MusicSearchRepository {
    suspend fun searchMusic(query: String): Flow<List<Music>>
}