package com.maikeruwu.substats.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.maikeruwu.substats.R
import com.maikeruwu.substats.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val mostPlayedSongs = binding.mostPlayedSongs
        mostPlayedSongs.textView.text = getString(R.string.title_most_played_songs)
        mostPlayedSongs.root.setOnClickListener {
            findNavController().navigate(R.id.navigation_statistics_most_played_songs)
        }

        val mostPlayedArtists = binding.mostPlayedArtists
        mostPlayedArtists.textView.text = getString(R.string.title_most_played_artists)
        mostPlayedArtists.root.setOnClickListener {
            findNavController().navigate(R.id.navigation_statistics_most_played_artists)
        }

        val mostPlayedAlbums = binding.mostPlayedAlbums
        mostPlayedAlbums.textView.text = getString(R.string.title_most_played_albums)
        mostPlayedAlbums.root.setOnClickListener {
            findNavController().navigate(R.id.navigation_statistics_most_played_albums)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}