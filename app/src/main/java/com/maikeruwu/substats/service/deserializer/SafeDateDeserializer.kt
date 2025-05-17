package com.maikeruwu.substats.service.deserializer

import android.annotation.SuppressLint
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date

class SafeDateDeserializer : JsonDeserializer<LocalDateTime?> {
    @SuppressLint("SimpleDateFormat")
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): LocalDateTime? {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val date: Date = dateFormat.parse(json.asString) ?: return null
            LocalDateTime.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}