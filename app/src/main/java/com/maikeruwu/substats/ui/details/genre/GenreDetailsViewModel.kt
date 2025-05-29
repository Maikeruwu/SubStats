package com.maikeruwu.substats.ui.details.genre

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maikeruwu.substats.model.data.Genre
import com.maikeruwu.substats.ui.details.AbstractDetailsViewModel

class GenreDetailsViewModel : AbstractDetailsViewModel() {
    private val _genre = MutableLiveData<Genre>()
    val genre: LiveData<Genre> = _genre

    fun setGenre(genre: Genre) {
        _genre.value = genre
    }
}