package com.maikeruwu.substats.ui.list.mostPlayedPlaylists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maikeruwu.substats.model.data.Playlist
import com.maikeruwu.substats.ui.list.AbstractListViewModel

class MostPlayedPlaylistsViewModel : AbstractListViewModel() {
    private val _playlists = MutableLiveData<List<Playlist>>().apply {
        value = mutableListOf()
    }
    val playlists: LiveData<List<Playlist>> = _playlists

    fun putPlaylist(playlist: Playlist) {
        val currentList = _playlists.value?.toMutableList() ?: mutableListOf()
        currentList.add(playlist)
        _playlists.value = currentList
            .sortedByDescending { it.entry?.sumOf { song -> song.playCount } }.toList()
    }
}