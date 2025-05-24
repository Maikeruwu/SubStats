package com.maikeruwu.substats.model.data

import java.io.Serializable
import java.time.LocalDateTime

data class Album(
    val id: String,
    val artist: String,
    val created: LocalDateTime,
    val coverArt: String,
    val year: Int,
    val genre: String,
    val sortName: String,
    val artistId: String,
    val name: String,
    val songCount: Int,
    val duration: Int,
    val song: List<Song>?
) : Serializable
