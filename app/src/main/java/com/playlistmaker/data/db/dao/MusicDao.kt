package com.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playlistmaker.data.db.entity.MusicEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(track: MusicEntity)

    @Delete
    suspend fun removeFromFavorites(track: MusicEntity)

    @Query("SELECT * FROM music_table")
    fun getAllFavoriteTracks(): Flow<List<MusicEntity>>

    @Query("SELECT trackId FROM music_table")
    suspend fun getAllFavoriteTrackId(): List<Long>
}