package com.playlistmaker.domain.repositories

import com.playlistmaker.data.db.entity.MusicEntity
import kotlinx.coroutines.flow.Flow


interface FavoritesRepository {
    suspend fun addToFavorites(track: MusicEntity)
    suspend fun removeFromFavorites(track: MusicEntity)
    fun getAllFavoriteTracks(): Flow<List<MusicEntity>>
}