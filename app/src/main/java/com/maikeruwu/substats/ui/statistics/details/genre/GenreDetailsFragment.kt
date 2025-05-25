package com.maikeruwu.substats.ui.statistics.details.genre

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.data.Genre
import com.maikeruwu.substats.ui.statistics.details.AbstractDetailsFragment

class GenreDetailsFragment :
    AbstractDetailsFragment<GenreDetailsViewModel>(GenreDetailsViewModel::class.java) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = super.init(inflater, container)

        val genre = arguments?.getSerializable("genre", Genre::class.java)

        if (genre == null) {
            viewModel.setErrorText(getString(R.string.error_no_entity))
            return root
        }
        viewModel.setGenre(genre)

        viewModel.genre.observe(viewLifecycleOwner) {
            setTopCard(it.value, null)
            addDetailCard(
                getString(R.string.label_song_count),
                it.songCount.toString(),
                ::onSongCountClick
            )
            addDetailCard(
                getString(R.string.label_album_count),
                it.albumCount.toString(),
                ::onAlbumCountClick
            )
        }
        return root
    }

    private fun onSongCountClick() {
        val bundle = Bundle().apply {
            putString("genreName", viewModel.genre.value?.value.orEmpty())
        }
        findNavController().navigate(R.id.navigation_statistics_most_played_songs, bundle)
    }

    private fun onAlbumCountClick() {
        val bundle = Bundle().apply {
            putString("genreName", viewModel.genre.value?.value.orEmpty())
        }
        findNavController().navigate(R.id.navigation_statistics_most_played_albums, bundle)
    }
}