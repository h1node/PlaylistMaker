package com.playlistmaker.presentation.ui.search.viewmodel

import androidx.annotation.StringRes
import com.playlistmaker.domain.models.Music

sealed class SearchState {
    object Loading : SearchState()
    data class Content(val musicList: List<Music>) : SearchState()
    data class Empty(@StringRes val messageId: Int) : SearchState()
    data class Error(@StringRes val messageId: Int) : SearchState()
}