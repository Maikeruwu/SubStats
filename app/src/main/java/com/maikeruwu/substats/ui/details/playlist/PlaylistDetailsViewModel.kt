package com.maikeruwu.substats.ui.details.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maikeruwu.substats.model.data.Playlist
import com.maikeruwu.substats.ui.details.AbstractDetailsViewModel

class PlaylistDetailsViewModel : AbstractDetailsViewModel() {
    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    fun setPlaylist(playlist: Playlist) {
        _playlist.value = playlist
    }
}