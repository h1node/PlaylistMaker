package com.playlistmaker.domain.repositories

import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow


interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun savePlaylist(playlist: Playlist)
    suspend fun addTrackToPlaylist(playlistId: Long, track: Music): Boolean
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun getTracksForPlaylist(playlistId: Long): List<Music>
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    suspend fun deleteTrackIfUnused(trackId: Long)
    suspend fun deletePlaylist(playlistId: Long)
}