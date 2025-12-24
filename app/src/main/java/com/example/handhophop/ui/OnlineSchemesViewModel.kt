package com.example.handhophop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handhophop.data.ImageItem
import com.example.handhophop.data.ImageRepository
import com.example.handhophop.data.remote.NekoNetwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.min

data class ImageListState(
    val items: List<ImageItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val reachedEnd: Boolean = false
)

class OnlineSchemesViewModel(
    private val repo: ImageRepository = ImageRepository(NekoNetwork.api)
) : ViewModel() {

    companion object {
        private const val MAX_ITEMS = 20
        private const val PAGE_SIZE = 10
    }

    private val _state = MutableStateFlow(ImageListState())
    val state: StateFlow<ImageListState> = _state

    private var page = 1

    init {
        loadMore()
    }

    fun loadMore() {
        val cur = _state.value
        if (cur.isLoading || cur.reachedEnd) return

        val already = cur.items.size
        val remaining = MAX_ITEMS - already
        if (remaining <= 0) {
            _state.value = cur.copy(reachedEnd = true)
            return
        }

        val requestSize = min(PAGE_SIZE, remaining)

        viewModelScope.launch {
            _state.value = cur.copy(isLoading = true, error = null)
            try {
                val newItems = repo.loadPage(page, requestSize)
                val merged = cur.items + newItems
                val end = merged.size >= MAX_ITEMS
                page += 1

                _state.value = ImageListState(
                    items = merged,
                    isLoading = false,
                    error = null,
                    reachedEnd = end
                )
            } catch (e: Exception) {
                _state.value = cur.copy(isLoading = false, error = e.message ?: "Ошибка загрузки")
            }
        }
    }

    fun refresh() {
        page = 1
        _state.value = ImageListState()
        loadMore()
    }
}
