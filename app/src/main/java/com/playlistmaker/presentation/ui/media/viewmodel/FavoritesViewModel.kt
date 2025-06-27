package com.playlistmaker.presentation.ui.media.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.domain.usecase.GetAllFavoritesUseCase
import com.playlistmaker.presentation.ui.media.FavoritesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class FavoritesViewModel(
    private val getAllFavoritesUseCase: GetAllFavoritesUseCase
) : ViewModel() {

    private val _favoritesState = MutableStateFlow<FavoritesState>(FavoritesState.Empty)
    val favoritesState: StateFlow<FavoritesState> = _favoritesState

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            getAllFavoritesUseCase.execute().collect { tracks ->
                _favoritesState.value = if (tracks.isEmpty()) {
                    FavoritesState.Empty
                } else {
                    FavoritesState.Loaded(tracks)
                }
            }
        }
    }

    fun refresh() {
        loadFavorites()
    }
}