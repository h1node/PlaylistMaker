package com.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.playlistmaker.data.impl.MusicRepositoryImpl
import com.playlistmaker.data.impl.SearchHistoryRepositoryImpl
import com.playlistmaker.domain.repositories.MusicSearchRepository
import com.playlistmaker.domain.repositories.SearchHistoryRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<SharedPreferences> {
        androidContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get()) }
    single<MusicSearchRepository> { MusicRepositoryImpl(get()) }
}