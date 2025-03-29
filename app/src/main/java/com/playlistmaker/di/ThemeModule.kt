package com.playlistmaker.di

import com.playlistmaker.domain.repositories.ThemeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val themeModule = module {
    single { ThemeRepository(androidContext()) }
}