package com.maikeruwu.substats.service.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.maikeruwu.substats.model.response.SubsonicResponse
import com.maikeruwu.substats.model.response.SubsonicResponseError
import java.lang.reflect.Type

class SubsonicResponseDeserializer<T>(
    private val clazz: Class<T>
) : JsonDeserializer<SubsonicResponse<T>> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): SubsonicResponse<T> {
        val root = json.asJsonObject["subsonic-response"].asJsonObject

        val status = root["status"].asString
        val version = root["version"].asString
        val type = root["type"]?.asString
        val serverVersion = root["serverVersion"]?.asString
        val openSubsonic = root["openSubsonic"]?.asBoolean

        // Extrahiere das einzige Kind-Element (z.B. ping, musicFolders, ...)
        val dataEntry = root.entrySet().firstOrNull {
            it.key !in listOf("status", "version", "type", "serverVersion", "openSubsonic")
        }

        val data = dataEntry?.value.let { context.deserialize<T>(it, clazz) }
        val error = dataEntry?.value.let {
            context.deserialize<SubsonicResponseError>(
                it,
                SubsonicResponseError::class.java
            )
        }

        return SubsonicResponse(
            status,
            version,
            type.orEmpty(),
            serverVersion.orEmpty(),
            openSubsonic == true,
            data,
            error
        )
    }
}