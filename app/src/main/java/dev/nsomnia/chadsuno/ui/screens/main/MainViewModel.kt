package dev.nsomnia.chadsuno.ui.screens.main

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
class MainViewModel @Inject constructor(
    private val preferences: AppPreferences,
    private val sunoRepository: SunoRepository
) : ViewModel() {

    private val _isOnboardingComplete = MutableStateFlow<Boolean?>(null)
    val isOnboardingComplete: StateFlow<Boolean?> = _isOnboardingComplete.asStateFlow()

    private val _hasValidToken = MutableStateFlow<Boolean?>(null)
    val hasValidToken: StateFlow<Boolean?> = _hasValidToken.asStateFlow()

    init {
        viewModelScope.launch {
            val hasCookie = !preferences.cookie.first().isNullOrBlank()
            _isOnboardingComplete.value = hasCookie
            _hasValidToken.value = hasCookie
        }
    }

    fun completeOnboarding() {
        _isOnboardingComplete.value = true
        _hasValidToken.value = true
    }

    fun validateToken() {
        viewModelScope.launch {
            val result = sunoRepository.getQuota()
            result.onSuccess {
                _hasValidToken.value = true
                _isOnboardingComplete.value = true
            }
            result.onFailure {
                _hasValidToken.value = false
            }
        }
    }
}
