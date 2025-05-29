package com.maikeruwu.substats.ui.list.mostPlayedArtists

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
import com.maikeruwu.substats.service.assertServicesAvailable
import com.maikeruwu.substats.service.endpoint.SubsonicBrowsingService
import com.maikeruwu.substats.ui.list.AbstractListFragment
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
        if (viewModel.artistSongs.value.isNullOrEmpty()) {
            val browsingService = SubsonicApiProvider.createService(SubsonicBrowsingService::class)
            assertServicesAvailable(viewModel, browsingService)

            val jobs: MutableList<Job> = mutableListOf()
            val handler = getHandler(
                viewModel,
                viewModel.artistSongs.value.isNullOrEmpty(),
                jobs
            )

            val artistId = arguments?.getString("artistId")
            if (artistId != null) {
                lifecycleScope.launch(handler) {
                    showProgressOverlay(true)
                    val artistResponse = browsingService!!.getArtist(artistId)
                    processArtist(artistResponse.data, browsingService)

                    if (viewModel.artistSongs.value.isNullOrEmpty()) {
                        viewModel.setErrorText(getString(R.string.error_no_entries))
                    }
                }
                return root
            }

            lifecycleScope.launch(handler) {
                showProgressOverlay(true)
                val artistsResponse = browsingService!!.getArtists()

                Optional.ofNullable(artistsResponse.data)
                    .map { it.index }
                    .stream().flatMap { it.stream() }
                    .map { it.artist }.flatMap { it.stream() }
                    .forEach {
                        jobs.add(lifecycleScope.launch(handler) {
                            val artistResponse = browsingService.getArtist(it.id)
                            processArtist(artistResponse.data, browsingService)
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

    private suspend fun processArtist(
        artist: Artist?,
        browsingService: SubsonicBrowsingService
    ) {
        val toAdd: MutableList<Pair<Artist, List<Song>>> = mutableListOf()
        val jobs: MutableList<Job> = mutableListOf()
        val handler = getHandler(viewModel, viewModel.artistSongs.value.isNullOrEmpty(), jobs)

        Optional.ofNullable(artist?.album)
            .stream().flatMap { it?.stream() }
            .forEach {
                jobs.add(lifecycleScope.launch(handler) {
                    val albumResponse = browsingService.getAlbum(it.id)

                    Optional.ofNullable(albumResponse.data)
                        .map { it.song }
                        .ifPresent {
                            toAdd.add(artist!! to it)
                        }
                })
            }
        jobs.forEach { it.join() }
        toAdd.forEach { viewModel.putArtistSongs(it.first, it.second) }
        showProgressOverlay(false)
    }
}