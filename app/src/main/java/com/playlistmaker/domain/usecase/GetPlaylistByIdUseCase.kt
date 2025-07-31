package com.playlistmaker.domain.usecase

import com.playlistmaker.domain.repositories.PlaylistRepository


class GetPlaylistByIdUseCase(private val repo: PlaylistRepository) {
    suspend operator fun invoke(id: Long) = repo.getPlaylistById(id)
}