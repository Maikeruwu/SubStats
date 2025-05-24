package com.maikeruwu.substats.ui.statistics.list.mostPlayedAlbums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.data.Album
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.model.response.SearchResponse
import com.maikeruwu.substats.model.response.SubsonicResponse
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.endpoint.SubsonicBrowsingService
import com.maikeruwu.substats.service.endpoint.SubsonicSearchingService
import com.maikeruwu.substats.ui.statistics.list.AbstractListFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Optional

class MostPlayedAlbumsFragment() : AbstractListFragment<MostPlayedAlbumsViewModel>(
    MostPlayedAlbumsViewModel::class.java
) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = init(inflater, container, viewModel)
        viewModel.albumSongs.observe(viewLifecycleOwner) {
            binding.recyclerView.adapter =
                AlbumSongListAdapter(it, getString(R.string.never), ::onItemClick)
        }

        var offset = 0
        val limit = 20
        val searchingService = SubsonicApiProvider.createService(SubsonicSearchingService::class)
        val browsingService = SubsonicApiProvider.createService(SubsonicBrowsingService::class)

        if (searchingService == null || browsingService == null) {
            viewModel.setErrorText(getString(R.string.invalid_base_url))
            return root
        }

        val jobs: MutableList<Job> = mutableListOf()
        val handler = getHandler(
            viewModel,
            viewModel.albumSongs.value.isNullOrEmpty(),
            jobs
        )

        lifecycleScope.launch(handler) {
            if (viewModel.albumSongs.value.isNullOrEmpty()) {
                showProgressOverlay(true)
                var response: SubsonicResponse<SearchResponse>? = null

                do {
                    val toAdd: MutableList<Pair<Album, List<Song>>> = mutableListOf()
                    response = searchingService.search("", 0, 0, limit, offset, 0, 0)
                    Optional.ofNullable(response.data)
                        .map { it.album }
                        .stream().flatMap { it.stream() }
                        .forEach {
                            offset += limit

                            jobs.add(lifecycleScope.launch(handler) {
                                val albumResponse = browsingService.getAlbum(it.id)

                                if (albumResponse.data != null && albumResponse.data.song != null) {
                                    toAdd.add(albumResponse.data to albumResponse.data.song)
                                }
                            })

                        }
                    jobs.forEach { it.join() }

                    toAdd.forEach {
                        viewModel.putAlbumSongs(it.first, it.second)
                    }
                    showProgressOverlay(false)
                } while (response.data?.song?.isNotEmpty() == true)
            }
        }
        return root
    }

    override fun onItemClick(position: Int) {
        val bundle = Bundle().apply {
            putSerializable("album", viewModel.albumSongs.value?.toList()?.get(position)?.first)
        }
        findNavController().navigate(R.id.navigation_album_details, bundle)
    }
}