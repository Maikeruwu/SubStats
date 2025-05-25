package com.maikeruwu.substats.ui.statistics.details.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.data.Album
import com.maikeruwu.substats.service.formatDate
import com.maikeruwu.substats.service.formatDuration
import com.maikeruwu.substats.ui.statistics.details.AbstractDetailsFragment

class AlbumDetailsFragment :
    AbstractDetailsFragment<AlbumDetailsViewModel>(AlbumDetailsViewModel::class.java) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = super.init(inflater, container)

        val album = arguments?.getSerializable("album", Album::class.java)

        if (album == null) {
            viewModel.setErrorText(getString(R.string.error_no_entity))
            return root
        }
        viewModel.setAlbum(album)

        viewModel.album.observe(viewLifecycleOwner) {
            setTopCard(it.name, it.coverArt)
            addDetailCard(getString(R.string.label_artist), it.artist, ::onArtistClick)
            addDetailCard(
                getString(R.string.label_song_count),
                it.songCount.toString(),
                ::onSongCountClick
            )
            addDetailCard(getString(R.string.label_created), it.created.formatDate())
            addDetailCard(getString(R.string.label_duration), it.duration.formatDuration())
            addDetailCard(getString(R.string.label_year), it.year.toString())
            addDetailCard(getString(R.string.label_genre), it.genre, ::onGenreClick)
        }
        return root
    }

    private fun onArtistClick() {
        val bundle = Bundle().apply {
            putString("artistId", viewModel.album.value?.artistId.orEmpty())
        }
        findNavController().navigate(R.id.navigation_statistics_most_played_artists, bundle)
    }

    private fun onSongCountClick() {
        val bundle = Bundle().apply {
            putString("albumId", viewModel.album.value?.id.orEmpty())
        }
        findNavController().navigate(R.id.navigation_statistics_most_played_songs, bundle)
    }

    private fun onGenreClick() {
        val bundle = Bundle().apply {
            putString("genreName", viewModel.album.value?.genre.orEmpty())
        }
        findNavController().navigate(R.id.navigation_statistics_most_played_genres, bundle)
    }
}