package com.maikeruwu.substats.ui.statistics.list.mostPlayedSongs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.data.Playlist
import com.maikeruwu.substats.model.response.SearchResponse
import com.maikeruwu.substats.model.response.SubsonicResponse
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.assertServicesAvailable
import com.maikeruwu.substats.service.endpoint.SubsonicBrowsingService
import com.maikeruwu.substats.service.endpoint.SubsonicSearchingService
import com.maikeruwu.substats.ui.statistics.list.AbstractListFragment
import kotlinx.coroutines.launch
import java.util.Optional

class MostPlayedSongsFragment : AbstractListFragment<MostPlayedSongsViewModel>(
    MostPlayedSongsViewModel::class.java
) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = init(inflater, container, viewModel)
        viewModel.songs.observe(viewLifecycleOwner) {
            binding.recyclerView.adapter =
                SongListAdapter(it, getString(R.string.never), ::onItemClick)
        }
        if (viewModel.songs.value.isNullOrEmpty()) {
            val handler = getHandler(viewModel, viewModel.songs.value.isNullOrEmpty())

            val albumId = arguments?.getString("albumId")
            if (albumId != null) {
                val browsingService =
                    SubsonicApiProvider.createService(SubsonicBrowsingService::class)
                assertServicesAvailable(viewModel, browsingService)

                lifecycleScope.launch(handler) {
                    showProgressOverlay(true)
                    val response = browsingService!!.getAlbum(albumId)

                    Optional.ofNullable(response.data)
                        .map { it.song }
                        .ifPresentOrElse(
                            {
                                viewModel.putSongs(it!!)
                                showProgressOverlay(false)
                            },
                            { viewModel.setErrorText(getString(R.string.error_no_entries)) }
                        )
                }
                return root
            }

            val playlist = arguments?.getSerializable("playlist", Playlist::class.java)
            if (playlist != null) {
                Optional.ofNullable(playlist.entry).ifPresentOrElse(
                    { viewModel.putSongs(it) },
                    { viewModel.setErrorText(getString(R.string.error_no_entries)) }
                )
                return root
            }

            val genreName = arguments?.getString("genreName")

            val searchingService =
                SubsonicApiProvider.createService(SubsonicSearchingService::class)
            assertServicesAvailable(viewModel, searchingService)

            lifecycleScope.launch(handler) {
                showProgressOverlay(true)
                val limit = 20
                var offset = 0
                var response: SubsonicResponse<SearchResponse>? = null

                do {
                    response = searchingService!!.search("", 0, 0, 0, 0, limit, offset)

                    val songs = response.data?.song.orEmpty()
                    if (songs.isNotEmpty()) {
                        offset += limit
                        viewModel.putSongs(songs.filter { genreName == null || it.genre == genreName })
                        showProgressOverlay(false)
                    }
                } while (songs.isNotEmpty())

                if (viewModel.songs.value.isNullOrEmpty()) {
                    viewModel.setErrorText(getString(R.string.error_no_entries))
                }
            }
        }
        return root
    }

    override fun onItemClick(position: Int) {
        val bundle = Bundle().apply {
            putSerializable("song", viewModel.songs.value?.get(position))
        }
        findNavController().navigate(R.id.navigation_song_details, bundle)
    }
}