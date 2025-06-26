package com.playlistmaker.core

//import android.content.Context
//import android.content.SharedPreferences
//import com.playlistmaker.data.api.ApiClient
//import com.playlistmaker.data.api.MusicApi
//import com.playlistmaker.data.impl.MusicRepositoryImpl
//import com.playlistmaker.data.impl.SearchHistoryRepositoryImpl
//import com.playlistmaker.domain.usecase.ClearSearchHistoryUseCase
//import com.playlistmaker.domain.usecase.GetSearchHistoryUseCase
//import com.playlistmaker.domain.usecase.ManageSearchHistoryUseCase
//import com.playlistmaker.domain.usecase.SearchMusicUseCase
//
//
//object Creator {
//    private lateinit var sharedPreferences: SharedPreferences
//    private lateinit var apiClient: MusicApi
//
//    fun initialize(context: Context) {
//        if (!::sharedPreferences.isInitialized) {
//            sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        }
//        if (!::apiClient.isInitialized) {
//            apiClient = ApiClient().createRetrofit().create(MusicApi::class.java)
//        }
//    }
//
//    fun provideSearchMusicUseCase(): SearchMusicUseCase {
//        val musicRepository = MusicRepositoryImpl(apiClient)
//        return SearchMusicUseCase(musicRepository)
//    }
//
//    fun provideManageSearchHistoryUseCase(): ManageSearchHistoryUseCase {
//        val repository = SearchHistoryRepositoryImpl(sharedPreferences)
//        return ManageSearchHistoryUseCase(repository)
//    }
//
//    fun provideGetSearchHistoryUseCase(): GetSearchHistoryUseCase {
//        val repository = SearchHistoryRepositoryImpl(sharedPreferences)
//        return GetSearchHistoryUseCase(repository)
//    }
//
//    fun provideClearSearchHistoryUseCase(): ClearSearchHistoryUseCase {
//        val repository = SearchHistoryRepositoryImpl(sharedPreferences)
//        return ClearSearchHistoryUseCase(repository)
//    }
//}