package com.maikeruwu.substats.model.data

import java.io.Serializable

data class Genre(
    val value: String,
    val songCount: Int,
    val albumCount: Int
) : Serializable
