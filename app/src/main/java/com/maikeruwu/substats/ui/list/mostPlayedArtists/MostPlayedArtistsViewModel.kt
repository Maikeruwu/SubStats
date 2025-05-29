package com.maikeruwu.substats.ui.list.mostPlayedArtists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maikeruwu.substats.model.data.Artist
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.ui.list.AbstractListViewModel

class MostPlayedArtistsViewModel : AbstractListViewModel() {
    private val _artistSongs = MutableLiveData<Map<Artist, List<Song>>>().apply {
        value = mutableMapOf()
    }
    val artistSongs: LiveData<Map<Artist, List<Song>>> = _artistSongs

    fun putArtistSongs(artist: Artist, songs: List<Song>) {
        val currentMap = _artistSongs.value?.toMutableMap() ?: mutableMapOf()
        currentMap[artist] = songs
        _artistSongs.value = currentMap.toList()
            .sortedByDescending { (_, songs) -> songs.sumOf { song -> song.playCount } }.toMap()
    }
}