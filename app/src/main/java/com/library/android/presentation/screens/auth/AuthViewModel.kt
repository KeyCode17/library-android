package com.library.android.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.library.android.domain.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Owns the auth state. Each screen gets its own instance but they all reflect the same
 * Singleton [AuthRepository] (the token store is the shared source of truth), so logging in on
 * one screen is observed by the next via [refreshSession].
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Anonymous)
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        refreshSession()
    }

    /** Re-derives state from the stored token: validates it via `/auth/me` or stays anonymous. */
    fun refreshSession() {
        viewModelScope.launch {
            if (!authRepository.hasSession()) {
                _state.value = AuthUiState.Anonymous
                return@launch
            }
            _state.value = AuthUiState.Loading
            authRepository.currentUser()
                .onSuccess { _state.value = AuthUiState.Authenticated(it) }
                .onFailure {
                    authRepository.logout()
                    _state.value = AuthUiState.Anonymous
                }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            authRepository.login(email, password)
                .onSuccess { _state.value = AuthUiState.Authenticated(it) }
                .onFailure { _state.value = AuthUiState.Error(it.message ?: DEFAULT_ERROR) }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            authRepository.register(email, password)
                .onSuccess { logInAfterRegister(email, password) }
                .onFailure { _state.value = AuthUiState.Error(it.message ?: DEFAULT_ERROR) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.value = AuthUiState.Anonymous
        }
    }

    // Registration creates a member but returns no token; log in for a seamless first session.
    private suspend fun logInAfterRegister(email: String, password: String) {
        authRepository.login(email, password)
            .onSuccess { _state.value = AuthUiState.Authenticated(it) }
            .onFailure { _state.value = AuthUiState.Error(it.message ?: DEFAULT_ERROR) }
    }

    private companion object {
        const val DEFAULT_ERROR = "Something went wrong"
    }
}
