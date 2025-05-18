package com.maikeruwu.substats.model.data

import java.time.LocalDateTime

data class Song(
    val id: String,
    val parent: String,
    val discNumber: Int,
    val title: String,
    val artist: String,
    val isDir: Boolean,
    val album: String,
    val year: Int,
    val size: Long,
    val contentType: String,
    val suffix: String,
    val duration: Int,
    val bitRate: Int,
    val path: String,
    val isVideo: Boolean,
    val albumId: String,
    val artistId: String,
    val type: String,
    val created: LocalDateTime,
    val genre: String,
    val coverArt: String,
    val playCount: Int,
    val played: LocalDateTime?,
    val sortName: String
)
