package com.maikeruwu.substats.service

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.maikeruwu.substats.model.subsonic.SubsonicResponse
import com.maikeruwu.substats.model.subsonic.system.PingResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SubsonicApiProvider {

    private val BASE_URL = SecureStorage.getBaseURL().orEmpty()

    private val gson = GsonBuilder().registerTypeAdapter(
        TypeToken.getParameterized(
            SubsonicResponse::class.java,
            PingResponse::class.java
        ).type, SubsonicResponseDeserializer(PingResponse::class.java)
    ).create()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val auth = SubsonicAuthInterceptor(
        SecureStorage.getApiKey().orEmpty()
    )

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(auth)
        .build()

    fun <T> createService(serviceClass: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient)
            .build()
            .create(serviceClass)
    }
}
