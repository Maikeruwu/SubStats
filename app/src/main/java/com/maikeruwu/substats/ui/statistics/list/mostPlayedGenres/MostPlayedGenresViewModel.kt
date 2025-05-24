package com.maikeruwu.substats.ui.statistics.list.mostPlayedGenres

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maikeruwu.substats.model.data.Genre
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.ui.statistics.list.AbstractListViewModel

class MostPlayedGenresViewModel : AbstractListViewModel() {
    private val _genreSongs = MutableLiveData<Map<Genre, List<Song>>>().apply {
        value = mutableMapOf()
    }
    val genreSongs: LiveData<Map<Genre, List<Song>>> = _genreSongs

    fun putGenreSongs(genre: Genre, songs: List<Song>) {
        val currentMap = _genreSongs.value?.toMutableMap() ?: mutableMapOf()
        currentMap[genre] = songs
        _genreSongs.value = currentMap.toList()
            .sortedByDescending { (_, songs) -> songs.sumOf { song -> song.playCount } }.toMap()
    }
}