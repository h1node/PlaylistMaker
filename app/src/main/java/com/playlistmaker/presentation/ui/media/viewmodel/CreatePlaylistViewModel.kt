package com.playlistmaker.presentation.ui.media.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.domain.usecase.SavePlaylistUseCase
import kotlinx.coroutines.launch


class CreatePlaylistViewModel(
    private val saveUseCase: SavePlaylistUseCase,
    private val state: SavedStateHandle
) : ViewModel() {

    val name = state.getStateFlow(KEY_NAME, "")
    val description = state.getStateFlow<String?>(KEY_DESC, null)
    val coverUri = state.getStateFlow<Uri?>(KEY_COVER, null)

    fun onNameChanged(new: String) {
        state[KEY_NAME] = new
    }

    fun onDescriptionChanged(new: String?) {
        state[KEY_DESC] = new
    }

    fun onCoverUriChanged(new: Uri?) {
        state[KEY_COVER] = new
    }

    fun savePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            saveUseCase(playlist)
        }
    }

    companion object {
        private const val KEY_NAME = "playlist_name"
        private const val KEY_DESC = "playlist_desc"
        private const val KEY_COVER = "playlist_cover_uri"
    }
}