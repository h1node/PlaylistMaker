package com.playlistmaker.core

import android.app.Application
import com.playlistmaker.di.networkModule
import com.playlistmaker.di.repositoryModule
import com.playlistmaker.di.themeModule
import com.playlistmaker.di.useCaseModule
import com.playlistmaker.di.viewModelModule
import com.playlistmaker.domain.repositories.ThemeRepository
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(networkModule, repositoryModule, useCaseModule, themeModule, viewModelModule)
        }
        get<ThemeRepository>().darkTheme = get<ThemeRepository>().darkTheme
    }
}