package com.playlistmaker.data.impl

import com.playlistmaker.data.db.AppDatabase
import com.playlistmaker.data.db.entity.MusicEntity
import com.playlistmaker.domain.repositories.FavoritesRepository
import kotlinx.coroutines.flow.Flow


class FavoritesRepositoryImpl(private val database: AppDatabase) : FavoritesRepository {

    private val musicDao = database.musicDao()

    override suspend fun addToFavorites(track: MusicEntity) {
        musicDao.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(track: MusicEntity) {
        musicDao.removeFromFavorites(track)
    }

    override fun getAllFavoriteTracks(): Flow<List<MusicEntity>> {
        return musicDao.getAllFavoriteTracks()
    }
}