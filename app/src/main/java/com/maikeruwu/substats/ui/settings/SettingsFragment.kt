package com.maikeruwu.substats.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.maikeruwu.substats.R
import com.maikeruwu.substats.databinding.FragmentSettingsBinding
import com.maikeruwu.substats.service.SecureStorage
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.endpoint.SubsonicSystemService
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val editTextBaseUrl = binding.editTextBaseUrl
        settingsViewModel.baseUrl.observe(viewLifecycleOwner) {
            editTextBaseUrl.setText(it)
        }

        val editTextApiKey = binding.editTextApiKey
        settingsViewModel.apiKey.observe(viewLifecycleOwner) {
            editTextApiKey.setText(it)
        }

        val buttonSave = binding.buttonSave
        buttonSave.setOnClickListener {
            saveSettings()
            settingsViewModel.setStatusText(resources.getString(R.string.settings_saved))
        }

        val buttonTest = binding.buttonTest
        buttonTest.setOnClickListener {
            saveSettings()
            val systemService = SubsonicApiProvider.createService(SubsonicSystemService::class)
            lifecycleScope.launch {
                settingsViewModel.setStatusText(
                    resources.getString(
                        R.string.settings_test_response,
                        try {
                            val res = systemService?.ping()

                            when (res?.status) {
                                "ok" -> resources.getString(android.R.string.ok)
                                else -> resources.getString(R.string.response_failed)
                            }
                        } catch (e: HttpException) {
                            "${e.code()} ${e.message()}"
                        }
                    )
                )
            }
        }

        settingsViewModel.statusText.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun saveSettings() {
        val settingsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]
        settingsViewModel.setBaseUrl(binding.editTextBaseUrl.text.toString())
        settingsViewModel.setApiKey(binding.editTextApiKey.text.toString())
        SecureStorage.set(SecureStorage.Key.BASE_URL, settingsViewModel.baseUrl.value ?: "")
        SecureStorage.set(SecureStorage.Key.API_KEY, settingsViewModel.apiKey.value ?: "")
    }
}