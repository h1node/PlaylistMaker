package com.playlistmaker.data.impl

import com.playlistmaker.data.api.MusicApi
import com.playlistmaker.data.models.ResultResponse
import com.playlistmaker.domain.models.Music
import com.playlistmaker.domain.repositories.MusicSearchRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MusicRepositoryImpl(
    private val api: MusicApi,
) : MusicSearchRepository {

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
}