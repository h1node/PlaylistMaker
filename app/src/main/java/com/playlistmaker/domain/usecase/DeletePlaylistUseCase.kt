package com.playlistmaker.domain.usecase

import com.playlistmaker.domain.repositories.PlaylistRepository


class DeletePlaylistUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }

}