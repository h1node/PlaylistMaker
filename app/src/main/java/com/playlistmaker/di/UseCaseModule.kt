package com.playlistmaker.di

import com.playlistmaker.domain.usecase.AddTrackToPlaylistUseCase
import com.playlistmaker.domain.usecase.ClearSearchHistoryUseCase
import com.playlistmaker.domain.usecase.GetAllFavoritesUseCase
import com.playlistmaker.domain.usecase.GetSearchHistoryUseCase
import com.playlistmaker.domain.usecase.ManageSearchHistoryUseCase
import com.playlistmaker.domain.usecase.PlaylistUseCase
import com.playlistmaker.domain.usecase.SavePlaylistUseCase
import com.playlistmaker.domain.usecase.SearchMusicUseCase
import org.koin.dsl.module


val useCaseModule = module {
    factory { SearchMusicUseCase(get()) }
    factory { ManageSearchHistoryUseCase(get()) }
    factory { GetSearchHistoryUseCase(get(), get()) }
    factory { ClearSearchHistoryUseCase(get()) }
    factory { GetAllFavoritesUseCase(get()) }
    factory { PlaylistUseCase(get()) }
    factory { SavePlaylistUseCase(get()) }
    factory { AddTrackToPlaylistUseCase(get()) }
}