package com.maikeruwu.substats.ui.statistics.details.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.data.Playlist
import com.maikeruwu.substats.service.formatBoolean
import com.maikeruwu.substats.service.formatDate
import com.maikeruwu.substats.service.formatDuration
import com.maikeruwu.substats.ui.statistics.details.AbstractDetailsFragment

class PlaylistDetailsFragment :
    AbstractDetailsFragment<PlaylistDetailsViewModel>(PlaylistDetailsViewModel::class.java) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = super.init(inflater, container)

        val playlist = arguments?.getSerializable("playlist", Playlist::class.java)

        if (playlist == null) {
            viewModel.setErrorText(getString(R.string.error_no_entity))
            return root
        }
        viewModel.setPlaylist(playlist)

        viewModel.playlist.observe(viewLifecycleOwner) {
            setTopCard(it.name, it.coverArt)
            addDetailCard(
                getString(R.string.label_song_count),
                it.songCount.toString(),
                ::onSongCountClick
            )
            addDetailCard(getString(R.string.label_duration), it.duration.formatDuration())
            addDetailCard(getString(R.string.label_created), it.created.formatDate())
            addDetailCard(getString(R.string.label_modified), it.changed.formatDate())
            addDetailCard(getString(R.string.label_owner), it.owner)
            addDetailCard(getString(R.string.label_public), getString(it.public.formatBoolean()))
        }
        return root
    }

    private fun onSongCountClick() {
        val bundle = Bundle().apply {
            putSerializable("playlist", viewModel.playlist.value)
        }
        findNavController().navigate(R.id.navigation_statistics_most_played_songs, bundle)
    }
}