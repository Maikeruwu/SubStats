package com.maikeruwu.substats.ui.statistics.list.mostPlayedAlbums

import androidx.lifecycle.MutableLiveData
import com.maikeruwu.substats.model.data.Album
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.ui.statistics.list.AbstractListViewModel

class MostPlayedAlbumsViewModel : AbstractListViewModel() {
    private val _albumSongs = MutableLiveData<Map<Album, List<Song>>>().apply {
        value = mutableMapOf()
    }
    val albumSongs: MutableLiveData<Map<Album, List<Song>>> = _albumSongs

    fun putAlbumSongs(album: Album, songs: List<Song>) {
        val currentMap = _albumSongs.value?.toMutableMap() ?: mutableMapOf()
        currentMap[album] = songs
        _albumSongs.value = currentMap.toList()
            .sortedByDescending { (_, songs) -> songs.sumOf { song -> song.playCount } }.toMap()
    }
}