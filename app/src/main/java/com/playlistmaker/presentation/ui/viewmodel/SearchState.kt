package com.playlistmaker.presentation.ui.viewmodel

import com.playlistmaker.domain.models.Music

sealed class SearchState {
    object Loading : SearchState()
    data class Content(val musicList: List<Music>) : SearchState()
    data class Empty(val message: String) : SearchState()
    data class Error(val message: String) : SearchState()
}