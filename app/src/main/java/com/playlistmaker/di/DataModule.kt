package com.playlistmaker.di

import androidx.room.Room
import com.playlistmaker.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val dataModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "music_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().musicDao() }
    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().playlistMusicDao() }
}