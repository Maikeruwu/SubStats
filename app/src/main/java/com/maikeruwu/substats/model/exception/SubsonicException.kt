package com.maikeruwu.substats.model.exception

class SubsonicException(
    val code: Int,
    override val message: String
) : Exception(message, null)