package com.playlistmaker.domain.usecase

import com.playlistmaker.domain.repositories.PlaylistRepository


class GetTracksForPlaylistUseCase(private val repo: PlaylistRepository) {
    suspend operator fun invoke(playlistId: Long) = repo.getTracksForPlaylist(playlistId)
}