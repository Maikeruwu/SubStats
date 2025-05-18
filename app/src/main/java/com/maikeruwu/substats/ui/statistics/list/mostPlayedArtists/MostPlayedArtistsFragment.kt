package com.maikeruwu.substats.ui.statistics.list.mostPlayedArtists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maikeruwu.substats.R
import com.maikeruwu.substats.databinding.FragmentStatisticsListBinding
import com.maikeruwu.substats.model.data.Artist
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.endpoint.SubsonicBrowsingService
import com.maikeruwu.substats.ui.statistics.list.observeText
import com.maikeruwu.substats.ui.statistics.list.showProgressOverlay
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.Optional

class MostPlayedArtistsFragment : Fragment() {

    private var _binding: FragmentStatisticsListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mostPlayedArtistsViewModel =
            ViewModelProvider(this)[MostPlayedArtistsViewModel::class.java]

        _binding = FragmentStatisticsListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.observeText(viewLifecycleOwner, mostPlayedArtistsViewModel)
        binding.showProgressOverlay(true)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        mostPlayedArtistsViewModel.artistSongs.observe(viewLifecycleOwner) {
            recyclerView.adapter = ArtistSongsAdapter(it, getString(R.string.never))
        }

        val browsingService = SubsonicApiProvider.createService(SubsonicBrowsingService::class)

        if (browsingService == null) {
            mostPlayedArtistsViewModel.setErrorText(getString(R.string.invalid_base_url))
            return root
        }

        val jobs: MutableList<Job> = mutableListOf()

        lifecycleScope.launch {
            try {
                if (mostPlayedArtistsViewModel.artistSongs.value.isNullOrEmpty()) {
                    val artistsResponse = browsingService.getArtists()

                    Optional.ofNullable(artistsResponse)
                        .filter { it.status == "ok" }
                        .map { it.data?.index }
                        .stream().flatMap { it?.stream() }
                        .map { it.artist }.flatMap { it?.stream() }
                        .forEach { artist ->
                            jobs.add(lifecycleScope.launch {
                                val innerJobs: MutableList<Job> = mutableListOf()
                                val toAdd: MutableList<Pair<Artist, List<Song>>> = mutableListOf()
                                val artistResponse = browsingService.getArtist(artist.id)

                                Optional.ofNullable(artistResponse)
                                    .filter { it.status == "ok" }
                                    .map { it.data?.album }
                                    .stream().flatMap { it?.stream() }
                                    .forEach {
                                        innerJobs.add(lifecycleScope.launch {
                                            val albumResponse = browsingService.getAlbum(it.id)

                                            Optional.ofNullable(albumResponse)
                                                .filter { it.status == "ok" }
                                                .map { it.data?.song }
                                                .ifPresent {
                                                    toAdd.add(artist to it)
                                                }
                                        })
                                    }
                                innerJobs.forEach { it.join() }

                                toAdd.forEach {
                                    mostPlayedArtistsViewModel.putArtistSongs(it.first, it.second)
                                }
                                binding.showProgressOverlay(false)
                            })
                        }
                    jobs.forEach { it.join() }
                }
            } catch (e: HttpException) {
                jobs.forEach {
                    it.cancel(e.message.orEmpty(), e)
                    it.cancelChildren()
                }
                mostPlayedArtistsViewModel.setErrorText(
                    getString(
                        R.string.response_error,
                        e.code(),
                        e.message()
                    )
                )
            } catch (e: Exception) {
                jobs.forEach {
                    it.cancel(e.message.orEmpty(), e)
                    it.cancelChildren()
                }
                mostPlayedArtistsViewModel.setErrorText(getString(R.string.response_failed))
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}