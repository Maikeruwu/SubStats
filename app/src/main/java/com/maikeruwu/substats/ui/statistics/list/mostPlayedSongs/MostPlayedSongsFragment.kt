package com.maikeruwu.substats.ui.statistics.list.mostPlayedSongs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.response.SearchResponse
import com.maikeruwu.substats.model.response.SubsonicResponse
import com.maikeruwu.substats.service.SubsonicApiProvider
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
            binding.recyclerView.adapter = SongAdapter(it, getString(R.string.never))
        }

        var offset = 0
        val limit = 20
        val searchingService = SubsonicApiProvider.createService(SubsonicSearchingService::class)

        if (searchingService == null) {
            viewModel.setErrorText(getString(R.string.invalid_base_url))
            return root
        }

        val handler = getHandler(
            viewModel,
            viewModel.songs.value.isNullOrEmpty()
        )

        lifecycleScope.launch(handler) {
            if (viewModel.songs.value.isNullOrEmpty()) {
                showProgressOverlay(true)
                var response: SubsonicResponse<SearchResponse>? = null

                do {
                    response = searchingService.search("", 0, 0, 0, 0, limit, offset)
                    Optional.ofNullable(response.data)
                        .map { it.song }
                        .ifPresent {
                            offset += limit
                            viewModel.putSongs(it)
                            showProgressOverlay(false)
                        }
                } while (response.data?.song?.isNotEmpty() == true)
            }
        }
        return root
    }
}