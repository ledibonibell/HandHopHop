package com.example.handhophop.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SelectedSchemeViewModel : ViewModel() {
    private val _selectedUrl = MutableStateFlow<String?>(null)
    val selectedUrl: StateFlow<String?> = _selectedUrl

    fun select(url: String) {
        _selectedUrl.value = url
    }

    fun clear() {
        _selectedUrl.value = null
    }
}
