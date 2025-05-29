package com.maikeruwu.substats.ui.details.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maikeruwu.substats.model.data.Album
import com.maikeruwu.substats.ui.details.AbstractDetailsViewModel

class AlbumDetailsViewModel : AbstractDetailsViewModel() {
    private val _album = MutableLiveData<Album>()
    val album: LiveData<Album> = _album

    fun setAlbum(album: Album) {
        _album.value = album
    }
}