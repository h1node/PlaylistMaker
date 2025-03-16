package com.playlistmaker.di

import com.playlistmaker.data.api.ApiClient
import com.playlistmaker.data.api.MusicApi
import org.koin.dsl.module
import retrofit2.Retrofit


val networkModule = module {
    single { ApiClient().createRetrofit() }
    single { get<Retrofit>().create(MusicApi::class.java) }
}
