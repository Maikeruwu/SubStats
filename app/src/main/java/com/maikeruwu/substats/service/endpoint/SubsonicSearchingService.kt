package com.maikeruwu.substats.service.endpoint

import com.maikeruwu.substats.model.response.SearchResponse
import com.maikeruwu.substats.model.response.SubsonicResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SubsonicSearchingService : AbstractSubsonicService {
    @GET("search3.view")
    suspend fun search(
        @Query("query") query: String = "",
        @Query("artistCount") artistCount: Int = 20,
        @Query("artistOffset") artistOffset: Int = 0,
        @Query("albumCount") albumCount: Int = 20,
        @Query("albumOffset") albumOffset: Int = 0,
        @Query("songCount") songCount: Int = 20,
        @Query("songOffset") songOffset: Int = 0
    ): SubsonicResponse<SearchResponse>
}