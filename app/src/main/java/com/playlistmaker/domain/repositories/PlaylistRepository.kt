package com.playlistmaker.domain.repositories

import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow


interface PlaylistRepository {
    suspend fun savePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(playlistId: Long, track: Music): Boolean
}