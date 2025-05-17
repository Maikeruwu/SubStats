package com.maikeruwu.substats.model.response.searching

import com.maikeruwu.substats.model.data.Artist
import com.maikeruwu.substats.model.data.Song

data class SearchResponse(
    val artist: List<Artist>,
    val album: List<Artist>,
    val song: List<Song>
)
