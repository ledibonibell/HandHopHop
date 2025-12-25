package com.example.handhophop.ui

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// ✅ DataStore прямо в этом файле (без новых файлов)
private val Context.dataStore by preferencesDataStore(name = "handhophop_prefs")
private val KEY_SELECTED_SCHEME_URL = stringPreferencesKey("selected_scheme_url")

class SelectedSchemeViewModel(app: Application) : AndroidViewModel(app) {

    private val context = app.applicationContext

    private val _selectedUrl = MutableStateFlow<String?>(null)
    val selectedUrl: StateFlow<String?> = _selectedUrl

    init {
        // ✅ восстановление при старте приложения
        viewModelScope.launch {
            context.dataStore.data
                .map { prefs -> prefs[KEY_SELECTED_SCHEME_URL] }
                .collect { url -> _selectedUrl.value = url }
        }
    }

    fun select(url: String) {
        _selectedUrl.value = url
        // ✅ сохранение
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_SELECTED_SCHEME_URL] = url
            }
        }
    }

    fun clear() {
        _selectedUrl.value = null
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs.remove(KEY_SELECTED_SCHEME_URL)
            }
        }
    }
}
