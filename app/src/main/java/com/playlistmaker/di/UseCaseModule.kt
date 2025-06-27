package com.playlistmaker.di

import com.playlistmaker.domain.usecase.ClearSearchHistoryUseCase
import com.playlistmaker.domain.usecase.GetAllFavoritesUseCase
import com.playlistmaker.domain.usecase.GetSearchHistoryUseCase
import com.playlistmaker.domain.usecase.ManageSearchHistoryUseCase
import com.playlistmaker.domain.usecase.SearchMusicUseCase
import org.koin.dsl.module


val useCaseModule = module {
    factory { SearchMusicUseCase(get()) }
    factory { ManageSearchHistoryUseCase(get()) }
    factory { GetSearchHistoryUseCase(get(), get()) }
    factory { ClearSearchHistoryUseCase(get()) }
    factory { GetAllFavoritesUseCase(get()) }
}