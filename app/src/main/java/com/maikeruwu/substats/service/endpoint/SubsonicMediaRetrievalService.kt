package com.maikeruwu.substats.service.endpoint

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface SubsonicMediaRetrievalService : AbstractSubsonicService {
    @GET("getCoverArt.view")
    suspend fun getCoverArt(
        @Query("id") id: String,
        @Query("size") size: Int = 380
    ): ResponseBody
}