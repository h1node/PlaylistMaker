package com.playlistmaker.core

import android.content.Context
import android.content.SharedPreferences
import com.playlistmaker.data.api.ApiClient
import com.playlistmaker.data.api.MusicApi
import com.playlistmaker.data.impl.MusicRepositoryImpl
import com.playlistmaker.data.impl.SearchHistoryRepositoryImpl
import com.playlistmaker.domain.repositories.MusicSearchRepository
import com.playlistmaker.domain.repositories.SearchHistoryRepository
import com.playlistmaker.domain.usecase.ClearSearchHistoryUseCase
import com.playlistmaker.domain.usecase.GetSearchHistoryUseCase
import com.playlistmaker.domain.usecase.ManageSearchHistoryUseCase
import com.playlistmaker.domain.usecase.SearchMusicUseCase

class Creator(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val apiClient: MusicApi = ApiClient().createRetrofit().create(MusicApi::class.java)

    private val musicRepository: MusicSearchRepository = MusicRepositoryImpl(apiClient)
    private val historyRepository: SearchHistoryRepository =
        SearchHistoryRepositoryImpl(sharedPreferences)

    val searchMusicUseCase = SearchMusicUseCase(musicRepository)
    val manageSearchHistoryUseCase = ManageSearchHistoryUseCase(historyRepository)
    val getSearchHistoryUseCase = GetSearchHistoryUseCase(historyRepository)
    val clearSearchHistoryUseCase = ClearSearchHistoryUseCase(historyRepository)
}