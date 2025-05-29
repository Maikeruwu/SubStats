package com.maikeruwu.substats.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maikeruwu.substats.model.data.SearchItem
import com.maikeruwu.substats.ui.list.AbstractListViewModel

class SearchViewModel : AbstractListViewModel() {
    private val _searchItems = MutableLiveData<List<SearchItem<Any>>>().apply {
        value = mutableListOf()
    }
    val searchItems: LiveData<List<SearchItem<Any>>> = _searchItems

    fun putSearchItems(searchItems: List<SearchItem<Any>>) {
        val currentList = _searchItems.value?.toMutableList() ?: mutableListOf()
        currentList.addAll(searchItems)
        _searchItems.value = currentList.sortedBy { it.id }
    }

    fun clearSearchItems() {
        _searchItems.value = mutableListOf()
    }
}