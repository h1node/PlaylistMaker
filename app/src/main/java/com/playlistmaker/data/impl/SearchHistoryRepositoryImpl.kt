package com.playlistmaker.data.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.repositories.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SearchHistoryRepository {

    private val gson = Gson()
    private val key = "search_history"

    override fun saveSearchHistory(trackList: List<Music>) {
        val json = gson.toJson(trackList)
        sharedPreferences.edit().putString(key, json).apply()
    }

    override fun getSearchHistory(): List<Music> {
        val json = sharedPreferences.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<Music>>() {}.type
        return gson.fromJson(json, type)
    }

    override fun clearSearchHistory() {
        sharedPreferences.edit().remove(key).apply()
    }
}