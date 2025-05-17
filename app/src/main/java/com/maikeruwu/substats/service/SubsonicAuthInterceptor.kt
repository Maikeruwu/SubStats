package com.maikeruwu.substats.service

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.net.SocketTimeoutException

class SubsonicAuthInterceptor(
    private val clientName: String = "SubStats",
    private val apiVersion: String = "1.16.1",
    private val format: String = "json"
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalUrl = original.url
        val newUrl = applyQueryParams(originalUrl.newBuilder()).build()
        val newRequest = original.newBuilder()
            .url(newUrl)
            .build()
        return try {
            chain.proceed(newRequest)
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            Response.Builder().build()
        }
    }

    fun applyQueryParams(urlBuilder: HttpUrl.Builder): HttpUrl.Builder {
        return urlBuilder
            .addQueryParameter("apiKey", SecureStorage.get(SecureStorage.Key.API_KEY))
            .addQueryParameter("v", apiVersion)
            .addQueryParameter("c", clientName)
            .addQueryParameter("f", format)
    }
}