package com.maikeruwu.substats.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.maikeruwu.substats.model.response.SubsonicResponse
import com.maikeruwu.substats.service.deserializer.SafeDateDeserializer
import com.maikeruwu.substats.service.deserializer.SubsonicResponseDeserializer
import com.maikeruwu.substats.service.endpoint.AbstractSubsonicService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import kotlin.reflect.KClass

object SubsonicApiProvider {

    const val REST_SUFFIX = "rest"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val auth = SubsonicAuthInterceptor(
        SecureStorage.get(SecureStorage.Key.API_KEY).orEmpty()
    )

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(auth)
        .build()

    private fun getGson(classes: List<Class<*>>): Gson {
        val gsonBuilder = GsonBuilder()
        classes.forEach {
            val typeToken = TypeToken.getParameterized(SubsonicResponse::class.java, it)
            gsonBuilder.registerTypeAdapter(
                typeToken.type,
                SubsonicResponseDeserializer(it)
            )
        }
        return gsonBuilder.registerTypeAdapter(LocalDateTime::class.java, SafeDateDeserializer())
            .create()
    }

    fun <T : AbstractSubsonicService> createService(serviceClass: KClass<T>): T? {
        // Get the return types of all methods in the service class
        val returnTypes = serviceClass.members.map { it.returnType }
            .filter { it.classifier == SubsonicResponse::class }
            .map { it.arguments.firstOrNull()?.type }
            .mapNotNull { it?.classifier as? KClass<*> }
            .map { it.java }
            .distinct()

        return try {
            Retrofit.Builder()
                .baseUrl(
                    SecureStorage.get(SecureStorage.Key.BASE_URL).orEmpty() + REST_SUFFIX + "/"
                )
                .addConverterFactory(GsonConverterFactory.create(getGson(returnTypes)))
                .client(httpClient)
                .build()
                .create(serviceClass.java)
        } catch (_: IllegalArgumentException) {
            println("Illegal Argument Exception, probably due to a misconfigured base URL")
            null
        }
    }
}
