package com.maikeruwu.substats.ui.statistics.mostPlayedSongs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maikeruwu.substats.model.data.Song

class MostPlayedSongsViewModel : ViewModel() {

    private val _songs = MutableLiveData<MutableList<Song>>().apply {
        value = mutableListOf<Song>()
    }
    val songs: LiveData<MutableList<Song>> = _songs

    private val _progressText = MutableLiveData<String>()
    val progressText: LiveData<String> = _progressText

    fun setProgressText(text: String) {
        _progressText.value = text
    }
}