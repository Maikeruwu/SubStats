package com.maikeruwu.substats.ui.details.artist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maikeruwu.substats.model.data.Artist
import com.maikeruwu.substats.ui.details.AbstractDetailsViewModel

class ArtistDetailsViewModel : AbstractDetailsViewModel() {
    private val _artist = MutableLiveData<Artist>()
    val artist: LiveData<Artist> = _artist

    fun setArtist(artist: Artist) {
        _artist.value = artist
    }
}