package com.maikeruwu.substats.ui.statistics.list.mostPlayedGenres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.response.SongsByGenreResponse
import com.maikeruwu.substats.model.response.SubsonicResponse
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.assertServicesAvailable
import com.maikeruwu.substats.service.endpoint.SubsonicBrowsingService
import com.maikeruwu.substats.service.endpoint.SubsonicSongListService
import com.maikeruwu.substats.ui.statistics.list.AbstractListFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Optional

class MostPlayedGenresFragment : AbstractListFragment<MostPlayedGenresViewModel>(
    MostPlayedGenresViewModel::class.java
) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = init(inflater, container, viewModel)
        viewModel.genreSongs.observe(viewLifecycleOwner) {
            binding.recyclerView.adapter =
                GenreSongListAdapter(it, getString(R.string.never), ::onItemClick)
        }
        if (viewModel.genreSongs.value.isNullOrEmpty()) {
            val browsingService = SubsonicApiProvider.createService(SubsonicBrowsingService::class)
            val songListService = SubsonicApiProvider.createService(SubsonicSongListService::class)
            assertServicesAvailable(viewModel, browsingService, songListService)

            val jobs: MutableList<Job> = mutableListOf()
            val handler = getHandler(
                viewModel,
                viewModel.genreSongs.value.isNullOrEmpty(),
                jobs
            )
            val genreName = arguments?.getString("genreName")

            lifecycleScope.launch(handler) {
                showProgressOverlay(true)
                val limit = 20
                val genresResponse = browsingService!!.getGenres()

                Optional.ofNullable(genresResponse.data)
                    .map { it.genre }
                    .stream().flatMap { it.stream() }
                    .filter { genreName == null || it.value == genreName }
                    .forEach { genre ->
                        jobs.add(lifecycleScope.launch(handler) {
                            var offset = 0
                            var response: SubsonicResponse<SongsByGenreResponse>? = null

                            do {
                                response =
                                    songListService!!.getSongsByGenre(genre.value, limit, offset)

                                val songs = response.data?.song.orEmpty()
                                if (songs.isNotEmpty()) {
                                    offset += limit
                                    viewModel.putGenreSongs(genre, songs)
                                    showProgressOverlay(false)
                                }
                            } while (songs.isNotEmpty())
                        })
                    }
                jobs.forEach { it.join() }

                if (viewModel.genreSongs.value.isNullOrEmpty()) {
                    viewModel.setErrorText(getString(R.string.error_no_entries))
                }
            }
        }
        return root
    }

    override fun onItemClick(position: Int) {
        val bundle = Bundle().apply {
            putSerializable("genre", viewModel.genreSongs.value?.toList()?.get(position)?.first)
        }
        findNavController().navigate(R.id.navigation_genre_details, bundle)
    }
}