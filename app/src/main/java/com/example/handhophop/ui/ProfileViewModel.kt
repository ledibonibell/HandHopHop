package com.example.handhophop.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.handhophop.data.ProfileRepository
import com.example.handhophop.data.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ProfileRepository(app.applicationContext)

    private val _state = MutableStateFlow(ProfileState(
        name = "abober4000",
        login = "abober4000",
        email = "egor@loh.ru",
        phone = "",
        language = "Русский",
        avatarUri = null
    ))
    val state: StateFlow<ProfileState> = _state

    init {
        viewModelScope.launch {
            repo.profileFlow.collect { loaded ->
                _state.value = loaded
            }
        }
    }

    fun update(transform: (ProfileState) -> ProfileState) {
        val newState = transform(_state.value)
        _state.value = newState
        viewModelScope.launch { repo.save(newState) }
    }
}
