package com.maikeruwu.substats.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maikeruwu.substats.service.SecureStorage

class SettingsViewModel : ViewModel() {
    private val _baseUrl = MutableLiveData<String>().apply {
        value = SecureStorage.get(SecureStorage.Key.BASE_URL)
    }
    val baseUrl: LiveData<String> = _baseUrl

    fun setBaseUrl(value: String) {
        // If value does not end with a slash, add one
        var value = value
        if (!value.endsWith("/")) {
            value += "/"
        }
        _baseUrl.value = value
    }

    private val _apiKey = MutableLiveData<String>().apply {
        value = SecureStorage.get(SecureStorage.Key.API_KEY)
    }
    val apiKey: LiveData<String> = _apiKey

    fun setApiKey(value: String) {
        _apiKey.value = value
    }

    private val _statusText = MutableLiveData<String>()
    val statusText: LiveData<String> = _statusText

    fun setStatusText(value: String) {
        _statusText.value = value
    }
}