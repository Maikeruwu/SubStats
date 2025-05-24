package com.maikeruwu.substats.ui.statistics.details.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            addDetailCard(getString(R.string.label_artist), it.artist)
            addDetailCard(getString(R.string.label_song_count), it.songCount.toString())
            addDetailCard(getString(R.string.label_file_created), it.created.formatDate())
            addDetailCard(getString(R.string.label_duration), it.duration.formatDuration())
            addDetailCard(getString(R.string.label_year), it.year.toString())
            addDetailCard(getString(R.string.label_genre), it.genre)
        }
        return root
    }
}