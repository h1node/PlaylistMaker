package com.playlistmaker.data

import com.playlistmaker.data.itunesdb.ResultResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApi {

    @GET("/search")
    fun getMusic(
        @Query("term") text: String
    ): Call<ResultResponse>
}