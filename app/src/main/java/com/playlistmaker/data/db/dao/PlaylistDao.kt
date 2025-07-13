package com.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playlistmaker.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_table WHERE id = :playlistId LIMIT 1")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?

    @Query("DELETE FROM playlist_table WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)
}