package com.playlistmaker.domain.usecase

import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.domain.repositories.PlaylistRepository
import kotlinx.coroutines.flow.Flow


class PlaylistUseCase(
    private val repository: PlaylistRepository
) {
    fun fetchPlaylists(): Flow<List<Playlist>> = repository.getAllPlaylists()
}