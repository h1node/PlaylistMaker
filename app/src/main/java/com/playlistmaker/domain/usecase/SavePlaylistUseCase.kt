package com.playlistmaker.domain.usecase

import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.domain.repositories.PlaylistRepository


class SavePlaylistUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlist: Playlist) {
        repository.savePlaylist(playlist)
    }
}