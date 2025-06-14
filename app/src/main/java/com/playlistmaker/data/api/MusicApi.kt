package com.playlistmaker.data.api

import com.playlistmaker.data.models.ResultResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface MusicApi {
    @GET("./search?entity=song")
    suspend fun getMusic(
        @Query("term") text: String
    ): ResultResponse
}