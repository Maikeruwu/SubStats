package com.maikeruwu.substats.ui.statistics.list.mostPlayedAlbums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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

class MostPlayedAlbumsFragment : AbstractListFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mostPlayedAlbumsViewModel =
            ViewModelProvider(this)[MostPlayedAlbumsViewModel::class.java]
        val root: View = init(inflater, container, mostPlayedAlbumsViewModel)
        mostPlayedAlbumsViewModel.albumSongs.observe(viewLifecycleOwner) {
            binding.recyclerView.adapter = AlbumSongsAdapter(it, getString(R.string.never))
        }

        var offset = 0
        val limit = 20
        val searchingService = SubsonicApiProvider.createService(SubsonicSearchingService::class)
        val browsingService = SubsonicApiProvider.createService(SubsonicBrowsingService::class)

        if (searchingService == null || browsingService == null) {
            mostPlayedAlbumsViewModel.setErrorText(getString(R.string.invalid_base_url))
            return root
        }

        val jobs: MutableList<Job> = mutableListOf()
        val handler = getHandler(
            mostPlayedAlbumsViewModel,
            mostPlayedAlbumsViewModel.albumSongs.value.isNullOrEmpty(),
            jobs
        )

        lifecycleScope.launch(handler) {
            if (mostPlayedAlbumsViewModel.albumSongs.value.isNullOrEmpty()) {
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
                        mostPlayedAlbumsViewModel.putAlbumSongs(it.first, it.second)
                    }
                    showProgressOverlay(false)
                } while (response.data?.song?.isNotEmpty() == true)

                /*val artistsResponse = browsingService.getArtists()

                Optional.ofNullable(artistsResponse.data)
                    .map { it.index }
                    .stream().flatMap { it.stream() }
                    .map { it.artist }.flatMap { it.stream() }
                    .forEach {
                        jobs.add(lifecycleScope.launch(handler) {
                            val innerJobs: MutableList<Job> = mutableListOf()
                            val toAdd: MutableList<Pair<Album, List<Song>>> = mutableListOf()
                            val artistResponse = browsingService.getArtist(it.id)

                            Optional.ofNullable(artistResponse.data)
                                .map { it.album }
                                .stream().flatMap { it.stream() }
                                .forEach { album ->
                                    innerJobs.add(lifecycleScope.launch(handler) {
                                        val albumResponse = browsingService.getAlbum(album.id)

                                        Optional.ofNullable(albumResponse.data)
                                            .map { it.song }
                                            .ifPresent {
                                                toAdd.add(album to it)
                                            }
                                    })
                                }
                            innerJobs.forEach { it.join() }

                            toAdd.forEach {
                                mostPlayedAlbumsViewModel.putAlbumSongs(it.first, it.second)
                            }
                            showProgressOverlay(false)
                        })
                    }
                jobs.forEach { it.join() }*/
            }
        }
        return root
    }
}