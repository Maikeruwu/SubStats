package com.maikeruwu.substats.service.endpoint

import com.maikeruwu.substats.model.data.Album
import com.maikeruwu.substats.model.data.Artist
import com.maikeruwu.substats.model.response.ArtistsResponse
import com.maikeruwu.substats.model.response.GenresResponse
import com.maikeruwu.substats.model.response.SubsonicResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SubsonicBrowsingService : AbstractSubsonicService {
    @GET("getArtists.view")
    suspend fun getArtists(): SubsonicResponse<ArtistsResponse>

    @GET("getGenres.view")
    suspend fun getGenres(): SubsonicResponse<GenresResponse>

    @GET("getArtist.view")
    suspend fun getArtist(
        @Query("id") id: String
    ): SubsonicResponse<Artist>

    @GET("getAlbum.view")
    suspend fun getAlbum(
        @Query("id") id: String
    ): SubsonicResponse<Album>
}