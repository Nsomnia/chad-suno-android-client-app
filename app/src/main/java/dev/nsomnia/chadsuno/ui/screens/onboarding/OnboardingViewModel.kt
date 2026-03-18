package dev.nsomnia.chadsuno.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nsomnia.chadsuno.data.local.AppPreferences
import dev.nsomnia.chadsuno.data.repository.SunoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferences: AppPreferences,
    private val sunoRepository: SunoRepository
) : ViewModel() {

    private val _cookie = MutableStateFlow<String?>(null)
    val cookie: StateFlow<String?> = _cookie.asStateFlow()

    private val _isValid = MutableStateFlow(false)
    val isValid: StateFlow<Boolean> = _isValid.asStateFlow()

    private val _isVerifying = MutableStateFlow(false)
    val isVerifying: StateFlow<Boolean> = _isVerifying.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            _cookie.value = preferences.cookie.first()
        }
    }

    fun updateCookie(value: String) {
        _cookie.value = value.ifBlank { null }
        _error.value = null
    }

    fun verifyAndSave() {
        viewModelScope.launch {
            val token = _cookie.value
            if (token.isNullOrBlank()) {
                _error.value = "Please enter your authentication token"
                return@launch
            }

            _isVerifying.value = true
            _error.value = null

            preferences.saveCookie(token)

            val result = sunoRepository.getQuota()
            
            result.onSuccess {
                _isValid.value = true
            }
            result.onFailure { e ->
                _error.value = "Authentication failed: ${e.message}. Please check your token."
            }

            _isVerifying.value = false
        }
    }
}
