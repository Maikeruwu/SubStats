package com.maikeruwu.substats.model.response.browsing

import com.maikeruwu.substats.model.data.Index

data class ArtistsResponse(
    val ignoredArticles: String, val index: List<Index>
)
