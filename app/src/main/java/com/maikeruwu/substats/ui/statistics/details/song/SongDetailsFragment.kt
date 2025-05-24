package com.maikeruwu.substats.ui.statistics.details.song

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.service.formatDate
import com.maikeruwu.substats.service.formatDuration
import com.maikeruwu.substats.ui.statistics.details.AbstractDetailsFragment

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
            viewModel.setErrorText(getString(com.maikeruwu.substats.R.string.error_no_entity))
            return root
        }
        viewModel.setSong(song)

        viewModel.song.observe(viewLifecycleOwner) {
            setTopCard(it.title, it.coverArt)
            addDetailCard(getString(com.maikeruwu.substats.R.string.label_artist), it.artist)
            addDetailCard(getString(com.maikeruwu.substats.R.string.label_album), it.album)
            addDetailCard(
                getString(com.maikeruwu.substats.R.string.label_disc_number),
                it.discNumber.toString()
            )
            addDetailCard(
                getString(com.maikeruwu.substats.R.string.label_duration),
                it.duration.formatDuration()
            )
            addDetailCard(getString(com.maikeruwu.substats.R.string.label_year), it.year.toString())
            addDetailCard(getString(com.maikeruwu.substats.R.string.label_genre), it.genre)
            addDetailCard(
                getString(com.maikeruwu.substats.R.string.play_count),
                it.playCount.toString()
            )
            addDetailCard(
                getString(com.maikeruwu.substats.R.string.last_played),
                it.played?.formatDate()
            )
            addDetailCard(
                getString(com.maikeruwu.substats.R.string.label_created),
                it.created.formatDate()
            )
            addDetailCard(getString(com.maikeruwu.substats.R.string.label_file_type), it.suffix)
        }
        return root
    }
}