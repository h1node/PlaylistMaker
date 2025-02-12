package com.playlistmaker.data.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import com.playlistmaker.data.api.MusicApi
import com.playlistmaker.data.models.ResultResponse
import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.repositories.MusicRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MusicRepositoryImpl(
    private val api: MusicApi,
    private val sharedPreferences: SharedPreferences
) : MusicRepository {

    override fun searchMusic(
        query: String,
        callback: (List<Music>) -> Unit,
        errorCallback: (Throwable) -> Unit
    ) {
        api.getMusic(query).enqueue(object : Callback<ResultResponse> {
            override fun onResponse(
                call: Call<ResultResponse>,
                response: Response<ResultResponse>
            ) {
                if (response.isSuccessful) {
                    val tracks =
                        response.body()?.results?.filter {
                            it.trackName != null && (it.trackTimeMillis ?: 0) > 0
                        }
                            ?: emptyList()
                    callback(tracks)
                } else {
                    errorCallback(Exception("Response failed"))
                }
            }

            override fun onFailure(call: Call<ResultResponse>, t: Throwable) {
                errorCallback(t)
            }

        })
    }

    override fun saveSearchHistory(trackList: List<Music>) {
        sharedPreferences.edit()
            .putString("SEARCH_HISTORY", Gson().toJson(trackList))
            .apply()
    }

    override fun getSearchHistory(): List<Music> {
        val json = sharedPreferences.getString("SEARCH_HISTORY", null)
        return json?.let { Gson().fromJson(it, Array<Music>::class.java).toList() } ?: emptyList()
    }

    override fun clearSearchHistory() {
        sharedPreferences.edit().remove("SEARCH_HISTORY").apply()
    }
}