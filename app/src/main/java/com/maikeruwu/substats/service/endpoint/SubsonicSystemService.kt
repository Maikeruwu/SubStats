package com.maikeruwu.substats.service.endpoint

import com.maikeruwu.substats.model.response.EmptyResponse
import com.maikeruwu.substats.model.response.SubsonicResponse
import retrofit2.http.GET

interface SubsonicSystemService : AbstractSubsonicService {
    @GET("ping.view")
    suspend fun ping(): SubsonicResponse<EmptyResponse>
}