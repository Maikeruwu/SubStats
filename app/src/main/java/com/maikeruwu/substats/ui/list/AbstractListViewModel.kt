package com.maikeruwu.substats.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class AbstractListViewModel : ViewModel() {
    private val _errorText = MutableLiveData<String>().apply {
        value = ""  // Initialize with an empty string
    }
    val errorText: LiveData<String> = _errorText

    fun setErrorText(text: String) {
        _errorText.value = text
    }
}