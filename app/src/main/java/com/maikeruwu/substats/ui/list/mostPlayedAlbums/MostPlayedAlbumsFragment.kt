package com.maikeruwu.substats.ui.list.mostPlayedAlbums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.data.Album
import com.maikeruwu.substats.model.response.SearchResponse
import com.maikeruwu.substats.model.response.SubsonicResponse
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.assertServicesAvailable
import com.maikeruwu.substats.service.endpoint.SubsonicBrowsingService
import com.maikeruwu.substats.service.endpoint.SubsonicSearchingService
import com.maikeruwu.substats.service.getHandler
import com.maikeruwu.substats.ui.list.AbstractListFragment
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
        viewModel.albums.observe(viewLifecycleOwner) {
            binding.recyclerView.adapter =
                AlbumListAdapter(it, getString(R.string.never), ::onItemClick)
        }
        if (viewModel.albums.value.isNullOrEmpty()) {
            val browsingService = SubsonicApiProvider.createService(SubsonicBrowsingService::class)
            assertServicesAvailable(viewModel, browsingService)

            val handler = getHandler(viewModel, viewModel.albums.value.isNullOrEmpty())

            val artistId = arguments?.getString("artistId")
            if (artistId != null) {
                lifecycleScope.launch(handler) {
                    showProgressOverlay(true)
                    val artistResponse = browsingService!!.getArtist(artistId)
                    processAlbums(artistResponse.data?.album.orEmpty(), browsingService)

                    if (viewModel.albums.value.isNullOrEmpty()) {
                        viewModel.setErrorText(getString(R.string.error_no_entries))
                    }
                }
                return root
            }

            val albumId = arguments?.getString("albumId")
            if (albumId != null) {
                lifecycleScope.launch(handler) {
                    showProgressOverlay(true)
                    val response = browsingService!!.getAlbum(albumId)
                    Optional.ofNullable(response.data)
                        .ifPresentOrElse(
                            { viewModel.putAlbum(it) },
                            { viewModel.setErrorText(getString(R.string.error_no_entries)) }
                        )
                    showProgressOverlay(false)
                }
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
                    response = searchingService!!.search("", 0, 0, limit, offset, 0, 0)
                    val albums = response.data?.album.orEmpty()
                        .filter { genreName == null || it.genre == genreName }
                    processAlbums(albums, browsingService!!)
                    offset += limit
                } while (albums.isNotEmpty())

                if (viewModel.albums.value.isNullOrEmpty()) {
                    viewModel.setErrorText(getString(R.string.error_no_entries))
                }
            }
        }
        return root
    }

    override fun onItemClick(position: Int) {
        val bundle = Bundle().apply {
            putSerializable("album", viewModel.albums.value?.get(position))
        }
        findNavController().navigate(R.id.navigation_album_details, bundle)
    }

    private suspend fun processAlbums(
        albums: List<Album>,
        browsingService: SubsonicBrowsingService
    ) {
        val toAdd: MutableList<Album> = mutableListOf()
        val jobs: MutableList<Job> = mutableListOf()
        val handler = getHandler(
            viewModel,
            viewModel.albums.value.isNullOrEmpty(),
            jobs
        )

        albums.forEach {
            jobs.add(lifecycleScope.launch(handler) {
                val albumResponse = browsingService.getAlbum(it.id)

                if (albumResponse.data != null && albumResponse.data.song != null) {
                    toAdd.add(albumResponse.data)
                }
            })
        }
        jobs.forEach { it.join() }
        toAdd.forEach { viewModel.putAlbum(it) }
        showProgressOverlay(false)
    }
}