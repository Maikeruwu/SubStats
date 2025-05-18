package com.maikeruwu.substats.ui.statistics.list.mostPlayedSongs

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
import com.maikeruwu.substats.model.response.SubsonicResponse
import com.maikeruwu.substats.model.response.searching.SearchResponse
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.endpoint.SubsonicSearchingService
import com.maikeruwu.substats.ui.statistics.list.observeText
import com.maikeruwu.substats.ui.statistics.list.showProgressOverlay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.Optional

class MostPlayedSongsFragment : Fragment() {

    private var _binding: FragmentStatisticsListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mostPlayedSongsViewModel =
            ViewModelProvider(this)[MostPlayedSongsViewModel::class.java]

        _binding = FragmentStatisticsListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.observeText(viewLifecycleOwner, mostPlayedSongsViewModel)
        binding.showProgressOverlay(true)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        mostPlayedSongsViewModel.songs.observe(viewLifecycleOwner) {
            recyclerView.adapter = SongAdapter(it, getString(R.string.never))
        }


        var offset = 0
        val limit = 20
        val searchingService = SubsonicApiProvider.createService(SubsonicSearchingService::class)

        if (searchingService == null) {
            mostPlayedSongsViewModel.setErrorText(getString(R.string.invalid_base_url))
            return root
        }

        lifecycleScope.launch {
            try {
                if (mostPlayedSongsViewModel.songs.value.isNullOrEmpty()) {
                    var response: SubsonicResponse<SearchResponse>? = null

                    do {
                        response = searchingService.search("", 0, 0, 0, 0, limit, offset)
                        Optional.ofNullable(response)
                            .filter { it.status == "ok" }
                            .map { it.data?.song }
                            .ifPresent {
                                offset += limit
                                mostPlayedSongsViewModel.putSongs(it)
                                binding.showProgressOverlay(false)
                            }
                    } while (response.status == "ok" && response.data?.song?.isNotEmpty() == true)
                }
            } catch (e: HttpException) {
                mostPlayedSongsViewModel.setErrorText(
                    getString(
                        R.string.response_error,
                        e.code(),
                        e.message()
                    )
                )
            } catch (_: Exception) {
                mostPlayedSongsViewModel.setErrorText(getString(R.string.response_failed))
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}