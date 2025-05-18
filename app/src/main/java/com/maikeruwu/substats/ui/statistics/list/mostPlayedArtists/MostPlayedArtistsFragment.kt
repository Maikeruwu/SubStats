package com.maikeruwu.substats.ui.statistics.list.mostPlayedArtists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.data.Artist
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.endpoint.SubsonicBrowsingService
import com.maikeruwu.substats.ui.statistics.list.AbstractListFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Optional

class MostPlayedArtistsFragment : AbstractListFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mostPlayedArtistsViewModel =
            ViewModelProvider(this)[MostPlayedArtistsViewModel::class.java]
        val root: View = init(inflater, container, mostPlayedArtistsViewModel)
        mostPlayedArtistsViewModel.artistSongs.observe(viewLifecycleOwner) {
            binding.recyclerView.adapter = ArtistSongsAdapter(it, getString(R.string.never))
        }

        val browsingService = SubsonicApiProvider.createService(SubsonicBrowsingService::class)

        if (browsingService == null) {
            mostPlayedArtistsViewModel.setErrorText(getString(R.string.invalid_base_url))
            return root
        }

        val jobs: MutableList<Job> = mutableListOf()
        val handler = getHandler(
            mostPlayedArtistsViewModel,
            mostPlayedArtistsViewModel.artistSongs.value.isNullOrEmpty(),
            jobs
        )

        lifecycleScope.launch(handler) {
            if (mostPlayedArtistsViewModel.artistSongs.value.isNullOrEmpty()) {
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
                                mostPlayedArtistsViewModel.putArtistSongs(it.first, it.second)
                            }
                            showProgressOverlay(false)
                        })
                    }
                jobs.forEach { it.join() }
            }
        }
        return root
    }
}