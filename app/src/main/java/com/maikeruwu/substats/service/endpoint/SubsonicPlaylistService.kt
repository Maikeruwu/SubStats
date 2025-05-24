package com.maikeruwu.substats.service.endpoint

import com.maikeruwu.substats.model.data.Playlist
import com.maikeruwu.substats.model.response.PlaylistsResponse
import com.maikeruwu.substats.model.response.SubsonicResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SubsonicPlaylistService : AbstractSubsonicService {
    @GET("getPlaylists.view")
    suspend fun getPlaylists(): SubsonicResponse<PlaylistsResponse>

    @GET("getPlaylist.view")
    suspend fun getPlaylist(
        @Query("id") id: String
    ): SubsonicResponse<Playlist>
}