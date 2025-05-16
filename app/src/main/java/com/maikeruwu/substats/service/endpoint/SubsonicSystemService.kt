package com.maikeruwu.substats.service.endpoint

import com.maikeruwu.substats.model.subsonic.SubsonicResponse
import com.maikeruwu.substats.model.subsonic.system.LicenseResponse
import com.maikeruwu.substats.model.subsonic.system.PingResponse
import retrofit2.http.GET

interface SubsonicSystemService {

    @GET("rest/ping.view")
    suspend fun ping(): SubsonicResponse<PingResponse>

    @GET("rest/getLicense.view")
    suspend fun getLicense(): LicenseResponse
}