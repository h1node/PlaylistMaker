package com.playlistmaker.di

import com.playlistmaker.domain.models.Music
import com.playlistmaker.presentation.ui.media.viewmodel.FavoritesViewModel
import com.playlistmaker.presentation.ui.player.viewmodel.AudioPlayerViewModel
import com.playlistmaker.presentation.ui.search.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { (track: Music?) ->
        AudioPlayerViewModel(get(), track, favoritesRepository = get())
    }
    viewModel { SearchViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
}