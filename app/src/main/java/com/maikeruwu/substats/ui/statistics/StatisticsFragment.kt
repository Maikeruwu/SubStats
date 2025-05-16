package com.maikeruwu.substats.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.maikeruwu.substats.databinding.FragmentStatisticsBinding
import com.maikeruwu.substats.service.SecureStorage
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.endpoint.SubsonicSystemService
import kotlinx.coroutines.launch

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
        val statisticsViewModel =
            ViewModelProvider(this)[StatisticsViewModel::class.java]

        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textStatistics
        statisticsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        SecureStorage.setApiKey("KjyPcoGfqW8s")
        SecureStorage.setBaseURL("https://maikeru.duckdns.org/apps/music/subsonic/")

        val systemService = SubsonicApiProvider.createService(SubsonicSystemService::class.java)

        binding.buttonPing.setOnClickListener {
            lifecycleScope.launch {
                val res = systemService.ping()
                textView.text = res.status
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}