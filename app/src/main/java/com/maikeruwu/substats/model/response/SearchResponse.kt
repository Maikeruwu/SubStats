package com.maikeruwu.substats.model.response

import com.maikeruwu.substats.model.data.Album
import com.maikeruwu.substats.model.data.Artist
import com.maikeruwu.substats.model.data.Song

data class SearchResponse(
    val artist: List<Artist>,
    val album: List<Album>,
    val song: List<Song>
)