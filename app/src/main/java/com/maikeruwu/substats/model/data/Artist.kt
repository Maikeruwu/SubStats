package com.maikeruwu.substats.model.data

import java.io.Serializable

data class Artist(
    val id: String,
    val name: String,
    val albumCount: Int,
    val sortName: String,
    val album: List<Album>?
) : Serializable
