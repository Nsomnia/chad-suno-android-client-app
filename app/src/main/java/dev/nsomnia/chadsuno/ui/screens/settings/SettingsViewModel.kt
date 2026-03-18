package dev.nsomnia.chadsuno.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nsomnia.chadsuno.data.local.AppPreferences
import dev.nsomnia.chadsuno.data.repository.SunoRepository
import dev.nsomnia.chadsuno.domain.model.QuotaInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: AppPreferences,
    private val sunoRepository: SunoRepository
) : ViewModel() {

    private val _cookie = MutableStateFlow<String?>(null)
    val cookie: StateFlow<String?> = _cookie.asStateFlow()

    private val _apiBaseUrl = MutableStateFlow<String?>(null)
    val apiBaseUrl: StateFlow<String?> = _apiBaseUrl.asStateFlow()

    private val _quota = MutableStateFlow<QuotaInfo?>(null)
    val quota: StateFlow<QuotaInfo?> = _quota.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saveSuccess = MutableStateFlow<Boolean?>(null)
    val saveSuccess: StateFlow<Boolean?> = _saveSuccess.asStateFlow()

    init {
        viewModelScope.launch {
            _cookie.value = preferences.cookie.first()
            _apiBaseUrl.value = preferences.apiBaseUrl.first()
            refreshQuota()
        }
    }

    fun updateCookie(value: String) {
        _cookie.value = value.ifBlank { null }
    }

    fun updateApiBaseUrl(value: String) {
        _apiBaseUrl.value = value.ifBlank { null }
    }

    fun saveSettings() {
        viewModelScope.launch {
            _cookie.value?.let { preferences.saveCookie(it) }
            _apiBaseUrl.value?.let { preferences.saveApiBaseUrl(it) }
            _saveSuccess.value = true
            refreshQuota()
        }
    }

    fun refreshQuota() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = sunoRepository.getQuota()
            result.onSuccess { quotaInfo ->
                _quota.value = quotaInfo
            }
            result.onFailure {
                _quota.value = null
            }
            _isLoading.value = false
        }
    }
}
