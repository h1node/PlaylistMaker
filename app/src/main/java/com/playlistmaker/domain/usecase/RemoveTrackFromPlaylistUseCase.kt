package com.playlistmaker.domain.usecase

import com.playlistmaker.domain.repositories.PlaylistRepository


class RemoveTrackFromPlaylistUseCase(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long, trackId: Long) {
        playlistRepository.removeTrackFromPlaylist(playlistId, trackId)
    }
}