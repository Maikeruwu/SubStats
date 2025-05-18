package com.maikeruwu.substats.ui.manage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ManageViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "WIP :("
    }
    val text: LiveData<String> = _text
}