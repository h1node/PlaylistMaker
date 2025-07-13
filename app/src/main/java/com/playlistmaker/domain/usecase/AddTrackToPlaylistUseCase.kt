package com.playlistmaker.domain.usecase

import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.repositories.PlaylistRepository


class AddTrackToPlaylistUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long, track: Music): Boolean {
        return repository.addTrackToPlaylist(playlistId, track)
    }
}