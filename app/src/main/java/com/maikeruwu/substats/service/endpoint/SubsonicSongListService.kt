package com.maikeruwu.substats.service.endpoint

import com.maikeruwu.substats.model.response.SongsByGenreResponse
import com.maikeruwu.substats.model.response.SubsonicResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SubsonicSongListService : AbstractSubsonicService {
    @GET("getSongsByGenre.view")
    suspend fun getSongsByGenre(
        @Query("genre") genre: String,
        @Query("count") count: Int = 10,
        @Query("offset") offset: Int = 0
    ): SubsonicResponse<SongsByGenreResponse>
}