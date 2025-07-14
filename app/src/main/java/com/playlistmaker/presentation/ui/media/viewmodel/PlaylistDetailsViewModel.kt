package com.playlistmaker.presentation.ui.media.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.domain.usecase.DeletePlaylistUseCase
import com.playlistmaker.domain.usecase.GetPlaylistByIdUseCase
import com.playlistmaker.domain.usecase.GetTracksForPlaylistUseCase
import com.playlistmaker.domain.usecase.RemoveTrackFromPlaylistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class PlaylistDetailsViewModel(
    private val getPlaylistById: GetPlaylistByIdUseCase,
    private val getTracksForPlaylist: GetTracksForPlaylistUseCase,
    private val removeTrackFromPlaylist: RemoveTrackFromPlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val playlistId: Long
) : ViewModel() {

    private val _playlist = MutableStateFlow<Playlist?>(null)
    val playlist: StateFlow<Playlist?> get() = _playlist

    private val _tracks = MutableStateFlow<List<Music>>(emptyList())
    val tracks: StateFlow<List<Music>> get() = _tracks

    fun deleteTrack(track: Music) {
        viewModelScope.launch {
            removeTrackFromPlaylist(playlistId, track.trackId)
            _playlist.value = getPlaylistById(playlistId)
            _tracks.value = getTracksForPlaylist(playlistId)
        }
    }

    suspend fun deletePlaylist() {
        deletePlaylistUseCase(playlistId)
    }

    fun refreshPlaylist() {
        viewModelScope.launch {
            _playlist.value = getPlaylistById(playlistId)
            _tracks.value = getTracksForPlaylist(playlistId)
        }
    }

    init {
        viewModelScope.launch {
            val playlist = getPlaylistById(playlistId)
            val tracks = getTracksForPlaylist(playlistId)
            _playlist.value = playlist
            _tracks.value = tracks
        }
    }
}