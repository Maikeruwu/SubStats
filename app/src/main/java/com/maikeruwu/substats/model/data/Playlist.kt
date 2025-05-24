package com.maikeruwu.substats.model.data

import java.io.Serializable
import java.time.LocalDateTime

data class Playlist(
    val id: String,
    val name: String,
    val owner: String,
    val public: Boolean,
    val songCount: Int,
    val duration: Int,
    val created: LocalDateTime,
    val changed: LocalDateTime,
    val coverArt: String,
    val entry: List<Song>?
) : Serializable
