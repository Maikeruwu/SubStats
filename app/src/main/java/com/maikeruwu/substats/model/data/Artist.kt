package com.maikeruwu.substats.model.data

data class Artist(
    val id: String,
    val name: String,
    val albumCount: Int,
    val sortName: String,
    val album: List<Album>?
)
