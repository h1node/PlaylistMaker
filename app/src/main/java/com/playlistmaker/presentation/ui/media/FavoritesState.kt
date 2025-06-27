package com.playlistmaker.presentation.ui.media

import com.playlistmaker.data.db.entity.MusicEntity


sealed interface FavoritesState {
    object Empty : FavoritesState
    data class Loaded(val tracks: List<MusicEntity>) : FavoritesState
}