package com.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playlistmaker.data.db.entity.PlaylistMusicEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaylistMusicDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistMusicEntity): Long

    @Query("SELECT trackId FROM playlist_music_table WHERE playlistId = :playlistId")
    suspend fun getTrackIdsForPlaylist(playlistId: Long): List<Long>

    @Query("SELECT COUNT(*) FROM playlist_music_table WHERE playlistId = :playlistId")
    fun countTracksInPlaylist(playlistId: Long): Flow<Int>
}