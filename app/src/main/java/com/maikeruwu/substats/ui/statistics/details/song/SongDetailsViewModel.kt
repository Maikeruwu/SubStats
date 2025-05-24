package com.maikeruwu.substats.ui.statistics.details.song

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.ui.statistics.details.AbstractDetailsViewModel

class SongDetailsViewModel : AbstractDetailsViewModel() {
    private val _song = MutableLiveData<Song>()
    val song: LiveData<Song> = _song

    fun setSong(song: Song) {
        _song.value = song
    }
}