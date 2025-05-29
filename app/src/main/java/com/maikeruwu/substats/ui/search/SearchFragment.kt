package com.maikeruwu.substats.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.maikeruwu.substats.R
import com.maikeruwu.substats.databinding.FragmentSearchBinding
import com.maikeruwu.substats.model.data.Album
import com.maikeruwu.substats.model.data.Artist
import com.maikeruwu.substats.model.data.SearchItem
import com.maikeruwu.substats.model.data.Song
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.assertServicesAvailable
import com.maikeruwu.substats.service.endpoint.SubsonicSearchingService
import com.maikeruwu.substats.service.getHandler
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this)[SearchViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.errorText.observe(viewLifecycleOwner) {
            binding.errorText.text = it
            showProgressOverlay(false, false)
            binding.errorText.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.searchItems.observe(viewLifecycleOwner) {
            binding.recyclerView.adapter = SearchAdapter(it, ::onItemClick)
        }

        val searchingService = SubsonicApiProvider.createService(SubsonicSearchingService::class)
        assertServicesAvailable(viewModel, searchingService)

        if (searchingService != null) {
            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        viewModel.clearSearchItems()
                        showProgressOverlay(true)
                        search(it, searchingService)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showProgressOverlay(
        showProgress: Boolean,
        showList: Boolean = !showProgress
    ) {
        binding.errorText.visibility = View.GONE
        binding.progressBar.visibility = if (showProgress) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (showList) View.VISIBLE else View.GONE
    }

    private fun onItemClick(position: Int) {
        val item = viewModel.searchItems.value?.get(position)
        when (item?.obj) {
            is Song -> {
                val bundle = Bundle().apply {
                    putSerializable("song", item.obj)
                }
                findNavController().navigate(R.id.navigation_song_details, bundle)
            }

            is Artist -> {
                val bundle = Bundle().apply {
                    putSerializable("artist", item.obj)
                }
                findNavController().navigate(R.id.navigation_artist_details, bundle)
            }

            is Album -> {
                val bundle = Bundle().apply {
                    putSerializable("album", item.obj)
                }
                findNavController().navigate(R.id.navigation_album_details, bundle)
            }

            else -> {
                Toast.makeText(context, getString(R.string.response_failed), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun <T> createSearchItem(obj: T): SearchItem<T> {
        val id = when (obj) {
            is Song -> obj.id
            is Artist -> obj.id
            is Album -> obj.id
            else -> "-1" // Default case for unknown types
        }
        val name = when (obj) {
            is Song -> obj.title
            is Artist -> obj.name
            is Album -> obj.name
            else -> getString(R.string.unknown)
        }
        val type = when (obj) {
            is Song -> getString(R.string.song)
            is Artist -> getString(R.string.artist)
            is Album -> getString(R.string.album)
            else -> getString(R.string.unknown)
        }
        val coverArt = when (obj) {
            is Song -> obj.coverArt
            is Album -> obj.coverArt
            else -> null
        }
        return SearchItem(id, name, type, coverArt, obj)
    }

    private fun search(query: String, searchingService: SubsonicSearchingService) {
        val handler = getHandler(viewModel, viewModel.searchItems.value.isNullOrEmpty())

        lifecycleScope.launch(handler) {
            val searchResponse = searchingService.search(query)

            searchResponse.data?.let { data ->
                val items = mutableListOf<SearchItem<Any>>()

                data.song.forEach { song ->
                    items.add(createSearchItem(song))
                }

                data.artist.forEach { artist ->
                    items.add(createSearchItem(artist))
                }

                data.album.forEach { album ->
                    items.add(createSearchItem(album))
                }

                viewModel.putSearchItems(items)
            } ?: run {
                viewModel.setErrorText(getString(R.string.error_no_entries))
            }
            showProgressOverlay(false)

            if (viewModel.searchItems.value.isNullOrEmpty()) {
                viewModel.setErrorText(getString(R.string.error_no_entries))
            }
        }
    }
}