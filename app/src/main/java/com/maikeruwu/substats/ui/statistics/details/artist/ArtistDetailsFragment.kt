package com.maikeruwu.substats.ui.statistics.details.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.maikeruwu.substats.model.data.Artist
import com.maikeruwu.substats.ui.statistics.details.AbstractDetailsFragment

class ArtistDetailsFragment :
    AbstractDetailsFragment<ArtistDetailsViewModel>(ArtistDetailsViewModel::class.java) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = super.init(inflater, container)

        val artist = arguments?.getSerializable("artist", Artist::class.java)

        if (artist == null) {
            viewModel.setErrorText(getString(com.maikeruwu.substats.R.string.error_no_entity))
            return root
        }
        viewModel.setArtist(artist)

        viewModel.artist.observe(viewLifecycleOwner) {
            setTopCard(it.name, it.album?.first()?.coverArt)
            addDetailCard(
                getString(com.maikeruwu.substats.R.string.label_album_count),
                it.albumCount.toString()
            )
        }
        return root
    }
}