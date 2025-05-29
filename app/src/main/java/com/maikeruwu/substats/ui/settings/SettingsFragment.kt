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
import com.maikeruwu.substats.model.exception.SubsonicException
import com.maikeruwu.substats.service.SecureStorage
import com.maikeruwu.substats.service.SubsonicApiProvider
import com.maikeruwu.substats.service.endpoint.SubsonicSystemService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    private fun getHandler(viewModel: SettingsViewModel): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            viewModel.setStatusText(
                when (exception) {
                    is HttpException -> {
                        getString(
                            R.string.response_error_code,
                            exception.code()
                        ) + if (exception.message().isNotEmpty()) getString(
                            R.string.response_error_message,
                            exception.message()
                        ) else ""
                    }

                    is SubsonicException -> {
                        getString(
                            R.string.response_error_code,
                            exception.code
                        ) + if (exception.message.isNotEmpty()) getString(
                            R.string.response_error_message,
                            exception.message
                        ) else ""
                    }

                    else -> {
                        getString(R.string.response_failed)
                    }
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.statusText.observe(viewLifecycleOwner) {
            if (it.isEmpty()) return@observe
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.setStatusText("")
        }

        val editTextBaseUrl = binding.editTextBaseUrl
        viewModel.baseUrl.observe(viewLifecycleOwner) {
            editTextBaseUrl.setText(it)
        }

        val editTextApiKey = binding.editTextApiKey
        viewModel.apiKey.observe(viewLifecycleOwner) {
            editTextApiKey.setText(it)
        }

        val buttonSave = binding.buttonSave
        buttonSave.setOnClickListener {
            saveSettings()
            viewModel.setStatusText(getString(R.string.settings_saved))
        }

        val buttonTest = binding.buttonTest
        buttonTest.setOnClickListener {
            saveSettings()
            val systemService = SubsonicApiProvider.createService(SubsonicSystemService::class)
            lifecycleScope.launch(getHandler(viewModel)) {
                val res = systemService?.ping()
                viewModel.setStatusText(
                    getString(
                        R.string.settings_test_response, when (res?.status) {
                            "ok" -> getString(android.R.string.ok)
                            else -> getString(R.string.response_failed)
                        }
                    )
                )
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveSettings() {
        viewModel.setBaseUrl(binding.editTextBaseUrl.text.toString())
        viewModel.setApiKey(binding.editTextApiKey.text.toString())
        SecureStorage.set(SecureStorage.Key.BASE_URL, viewModel.baseUrl.value ?: "")
        SecureStorage.set(SecureStorage.Key.API_KEY, viewModel.apiKey.value ?: "")
    }
}