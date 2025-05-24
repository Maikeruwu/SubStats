package com.maikeruwu.substats.ui.statistics.list.mostPlayedPlaylists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maikeruwu.substats.R
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.endpoint.SubsonicPlaylistService
import com.maikeruwu.substats.ui.statistics.list.AbstractListFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Optional

class MostPlayedPlaylistsFragment : AbstractListFragment<MostPlayedPlaylistsViewModel>(
    MostPlayedPlaylistsViewModel::class.java
) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = init(inflater, container, viewModel)
        viewModel.playlists.observe(viewLifecycleOwner) {
            binding.recyclerView.adapter =
                PlaylistListAdapter(it, getString(R.string.never), ::onItemClick)
        }

        val playlistService = SubsonicApiProvider.createService(SubsonicPlaylistService::class)

        if (playlistService == null) {
            viewModel.setErrorText(getString(R.string.invalid_base_url))
            return root
        }

        val jobs: MutableList<Job> = mutableListOf()
        val handler = getHandler(
            viewModel,
            viewModel.playlists.value.isNullOrEmpty(),
            jobs
        )

        lifecycleScope.launch(handler) {
            if (viewModel.playlists.value.isNullOrEmpty()) {
                showProgressOverlay(true)
                val playlistsResponse = playlistService.getPlaylists()

                Optional.ofNullable(playlistsResponse.data)
                    .map { it.playlist }
                    .stream().flatMap { it.stream() }
                    .forEach {
                        jobs.add(lifecycleScope.launch(handler) {
                            val response = playlistService.getPlaylist(it.id)

                            Optional.ofNullable(response.data)
                                .ifPresent {
                                    viewModel.putPlaylist(it)
                                    showProgressOverlay(false)
                                }
                        })
                    }
                jobs.forEach { it.join() }
            }
        }
        return root
    }

    override fun onItemClick(position: Int) {
        val bundle = Bundle().apply {
            putSerializable("playlist", viewModel.playlists.value?.firstOrNull())
        }
        findNavController().navigate(R.id.navigation_playlist_details, bundle)
    }
}