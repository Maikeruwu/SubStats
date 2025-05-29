package com.maikeruwu.substats.ui.details.song

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.service.formatDate
import com.maikeruwu.substats.service.formatDuration
import com.maikeruwu.substats.ui.details.AbstractDetailsFragment

class SongDetailsFragment :
    AbstractDetailsFragment<SongDetailsViewModel>(SongDetailsViewModel::class.java) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = super.init(inflater, container)

        val song = arguments?.getSerializable("song", Song::class.java)

        if (song == null) {
            viewModel.setErrorText(getString(R.string.error_no_entity))
            return root
        }
        viewModel.setSong(song)

        viewModel.song.observe(viewLifecycleOwner) {
            setTopCard(it.title, it.coverArt)
            addDetailCard(getString(R.string.label_artist), it.artist, ::onArtistClick)
            addDetailCard(getString(R.string.label_album), it.album, ::onAlbumClick)
            addDetailCard(
                getString(R.string.label_disc_number),
                it.discNumber.toString()
            )
            addDetailCard(
                getString(R.string.label_duration),
                it.duration.formatDuration()
            )
            addDetailCard(getString(R.string.label_year), it.year.toString())
            addDetailCard(getString(R.string.label_genre), it.genre, ::onGenreClick)
            addDetailCard(
                getString(R.string.play_count),
                it.playCount.toString()
            )
            addDetailCard(
                getString(R.string.last_played),
                it.played?.formatDate()
            )
            addDetailCard(
                getString(R.string.label_created),
                it.created.formatDate()
            )
            addDetailCard(getString(R.string.label_file_type), it.suffix)
        }
        return root
    }

    private fun onArtistClick() {
        val bundle = Bundle().apply {
            putString("artistId", viewModel.song.value?.artistId.orEmpty())
        }
        findNavController().navigate(R.id.navigation_statistics_most_played_artists, bundle)
    }

    private fun onAlbumClick() {
        val bundle = Bundle().apply {
            putString("albumId", viewModel.song.value?.albumId.orEmpty())
        }
        findNavController().navigate(R.id.navigation_statistics_most_played_albums, bundle)
    }

    private fun onGenreClick() {
        val bundle = Bundle().apply {
            putString("genreName", viewModel.song.value?.genre.orEmpty())
        }
        findNavController().navigate(R.id.navigation_statistics_most_played_genres, bundle)
    }
}