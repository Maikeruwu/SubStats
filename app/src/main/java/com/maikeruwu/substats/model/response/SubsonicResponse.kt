package com.maikeruwu.substats.model.response

data class SubsonicResponse<T>(
    val status: String,
    val version: String,
    val type: String,
    val serverVersion: String,
    val openSubsonic: Boolean,
    val data: T?
)