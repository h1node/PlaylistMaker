package com.playlistmaker.presentation.ui.media.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.domain.usecase.PlaylistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class PlaylistListViewModel(
    private val interactor: PlaylistUseCase
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists

    init {
        viewModelScope.launch {
            interactor.fetchPlaylists().collect { list ->
                _playlists.value = list
            }
        }
    }
}