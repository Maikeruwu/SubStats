package com.maikeruwu.substats.service

import okhttp3.Interceptor
import okhttp3.Response

class SubsonicAuthInterceptor(
    private val apiKey: String,
    private val clientName: String = "SubStats",
    private val apiVersion: String = "1.16.1",
    private val format: String = "json"
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalUrl = original.url

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("apiKey", apiKey)
            .addQueryParameter("v", apiVersion)
            .addQueryParameter("c", clientName)
            .addQueryParameter("f", format)
            .build()

        val newRequest = original.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}