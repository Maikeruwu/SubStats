package com.maikeruwu.substats.ui.statistics.list.mostPlayedSongs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.ui.statistics.list.ListViewModel

class MostPlayedSongsViewModel : ListViewModel() {

    private val _songs = MutableLiveData<List<Song>>().apply {
        value = mutableListOf<Song>()
    }
    val songs: LiveData<List<Song>> = _songs

    fun putSongs(songs: List<Song>) {
        val currentList = _songs.value?.toMutableList() ?: mutableListOf()
        currentList.addAll(songs)
        _songs.value = currentList.sortedByDescending { song -> song.playCount }
    }
}