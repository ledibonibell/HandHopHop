package com.example.handhophop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.handhophop.data.ImageRepository

class OnlineSchemesVmFactory(
    private val repo: ImageRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OnlineSchemesViewModel(repo) as T
    }
}
