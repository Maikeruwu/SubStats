package com.maikeruwu.substats.ui.statistics.list.mostPlayedArtists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.data.Artist
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.endpoint.SubsonicBrowsingService
import com.maikeruwu.substats.ui.statistics.list.AbstractListFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Optional

class MostPlayedArtistsFragment : AbstractListFragment<MostPlayedArtistsViewModel>(
    MostPlayedArtistsViewModel::class.java
) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = init(inflater, container, viewModel)
        viewModel.artistSongs.observe(viewLifecycleOwner) {
            binding.recyclerView.adapter =
                ArtistSongListAdapter(it, getString(R.string.never), ::onItemClick)
        }

        val browsingService = SubsonicApiProvider.createService(SubsonicBrowsingService::class)

        if (browsingService == null) {
            viewModel.setErrorText(getString(R.string.invalid_base_url))
            return root
        }

        val jobs: MutableList<Job> = mutableListOf()
        val handler = getHandler(
            viewModel,
            viewModel.artistSongs.value.isNullOrEmpty(),
            jobs
        )

        lifecycleScope.launch(handler) {
            if (viewModel.artistSongs.value.isNullOrEmpty()) {
                showProgressOverlay(true)
                val artistsResponse = browsingService.getArtists()

                Optional.ofNullable(artistsResponse.data)
                    .map { it.index }
                    .stream().flatMap { it.stream() }
                    .map { it.artist }.flatMap { it.stream() }
                    .forEach { artist ->
                        jobs.add(lifecycleScope.launch(handler) {
                            val innerJobs: MutableList<Job> = mutableListOf()
                            val toAdd: MutableList<Pair<Artist, List<Song>>> = mutableListOf()
                            val artistResponse = browsingService.getArtist(artist.id)

                            Optional.ofNullable(artistResponse.data)
                                .map { it.album }
                                .stream().flatMap { it?.stream() }
                                .forEach {
                                    innerJobs.add(lifecycleScope.launch(handler) {
                                        val albumResponse = browsingService.getAlbum(it.id)

                                        Optional.ofNullable(albumResponse.data)
                                            .map { it.song }
                                            .ifPresent {
                                                toAdd.add(artist to it)
                                            }
                                    })
                                }
                            innerJobs.forEach { it.join() }

                            toAdd.forEach {
                                viewModel.putArtistSongs(it.first, it.second)
                            }
                            showProgressOverlay(false)
                        })
                    }
                jobs.forEach { it.join() }

                if (viewModel.artistSongs.value.isNullOrEmpty()) {
                    viewModel.setErrorText(getString(R.string.error_no_entries))
                }
            }
        }
        return root
    }

    override fun onItemClick(position: Int) {
        val bundle = Bundle().apply {
            putSerializable("artist", viewModel.artistSongs.value?.toList()?.get(position)?.first)
        }
        findNavController().navigate(R.id.navigation_artist_details, bundle)
    }
}