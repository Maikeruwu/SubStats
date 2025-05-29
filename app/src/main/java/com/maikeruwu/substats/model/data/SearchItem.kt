package com.maikeruwu.substats.model.data

data class SearchItem<T>(
    val id: String,
    val name: String,
    val type: String,
    val coverArt: String? = null,
    val obj: T
)
