package com.example.rafiq.presentation.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun updateEmail(value: String) { _email.value = value }
    fun updatePassword(value: String) { _password.value = value }
    fun clearError() { _error.value = null }

    fun login(onSuccess: () -> Unit) {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _error.value = "Please enter email and password"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = authRepository.login(_email.value, _password.value)) {
                is AuthRepository.Result.Success -> onSuccess()
                is AuthRepository.Result.Error -> _error.value = result.message
            }

            _isLoading.value = false
        }
    }

    fun register(onSuccess: () -> Unit) {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _error.value = "Please enter email and password"
            return
        }

        if (_password.value.length < 6) {
            _error.value = "Password must be at least 6 characters"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = authRepository.register(_email.value, _password.value)) {
                is AuthRepository.Result.Success -> onSuccess()
                is AuthRepository.Result.Error -> _error.value = result.message
            }

            _isLoading.value = false
        }
    }

    fun continueAsGuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            when (val result = authRepository.guestLogin()) {
                is AuthRepository.Result.Success -> onSuccess()
                is AuthRepository.Result.Error -> onSuccess() // Still proceed even if guest fails
            }

            _isLoading.value = false
        }
    }
}
