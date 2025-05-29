package com.maikeruwu.substats.ui.list.mostPlayedAlbums

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maikeruwu.substats.model.data.Album
import com.maikeruwu.substats.ui.list.AbstractListViewModel

class MostPlayedAlbumsViewModel : AbstractListViewModel() {
    private val _albums = MutableLiveData<List<Album>>().apply {
        value = mutableListOf()
    }
    val albums: LiveData<List<Album>> = _albums

    fun putAlbum(album: Album) {
        val currentList = _albums.value?.toMutableList() ?: mutableListOf()
        currentList.add(album)
        _albums.value = currentList
            .sortedByDescending { it.song?.sumOf { song -> song.playCount } }.toList()
    }
}