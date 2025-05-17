package com.maikeruwu.substats.ui.statistics.mostPlayedSongs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maikeruwu.substats.R
import com.maikeruwu.substats.databinding.FragmentStatisticsMostPlayedSongsBinding
import com.maikeruwu.substats.model.response.SubsonicResponse
import com.maikeruwu.substats.model.response.searching.SearchResponse
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.endpoint.SubsonicSearchingService
import kotlinx.coroutines.launch
import java.util.Optional

class MostPlayedSongsFragment : Fragment() {

    private var _binding: FragmentStatisticsMostPlayedSongsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun showProgressOverlay(show: Boolean) {
        binding.progressOverlay.visibility = if (show) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mostPlayedSongsViewModel =
            ViewModelProvider(this)[MostPlayedSongsViewModel::class.java]

        _binding = FragmentStatisticsMostPlayedSongsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        showProgressOverlay(true)

        var offset = 0
        val limit = 20
        var totalSongs = 0

        val recyclerView = binding.recyclerView
        val searchingService = SubsonicApiProvider.createService(SubsonicSearchingService::class)

        lifecycleScope.launch {
            var response: SubsonicResponse<SearchResponse>? = null

            do {
                response = searchingService?.search("", 0, 0, 0, 0, limit, offset)
                Optional.ofNullable(response)
                    .filter { it.status.equals("ok") }
                    .map { it.data?.song }
                    .ifPresent {
                        totalSongs += it.size
                        offset += limit
                        mostPlayedSongsViewModel.songs.value?.addAll(it)
                        mostPlayedSongsViewModel.setProgressText(
                            getString(
                                R.string.progress_text,
                                totalSongs
                            )
                        )
                    }
            } while (!response?.status.equals("failed") && response?.data?.song?.isNotEmpty() == true)

            // Sort songs by play count
            mostPlayedSongsViewModel.songs.value?.sortByDescending { it.playCount }

            // Set LayoutManager
            recyclerView.layoutManager = LinearLayoutManager(context)

            // Set Adapter
            val adapter = SongAdapter(mostPlayedSongsViewModel.songs.value ?: emptyList())
            recyclerView.adapter = adapter

            showProgressOverlay(false)
        }

        val progressText = binding.progressText
        mostPlayedSongsViewModel.progressText.observe(viewLifecycleOwner) {
            progressText.text = it
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}