package com.library.android.presentation.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.library.android.domain.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UiState for account self-service (current email + a transient result/error + deletion flag). */
data class AccountUiState(
    val isLoading: Boolean = true,
    val email: String = "",
    val message: String? = null,
    val deleted: Boolean = false,
)

/** Self-service account actions: update email, change password, delete account. */
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AccountUiState())
    val state: StateFlow<AccountUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            authRepository.currentUser()
                .onSuccess { _state.value = AccountUiState(isLoading = false, email = it.email) }
                .onFailure { _state.value = AccountUiState(isLoading = false, message = it.message) }
        }
    }

    fun updateEmail(email: String) {
        viewModelScope.launch {
            authRepository.updateEmail(email)
                .onSuccess { _state.value = _state.value.copy(email = it.email, message = "Email updated") }
                .onFailure { _state.value = _state.value.copy(message = it.message) }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            authRepository.changePassword(currentPassword, newPassword)
                .onSuccess { _state.value = _state.value.copy(message = "Password changed") }
                .onFailure { _state.value = _state.value.copy(message = it.message) }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            authRepository.deleteAccount()
                .onSuccess { _state.value = _state.value.copy(deleted = true) }
                .onFailure { _state.value = _state.value.copy(message = it.message) }
        }
    }
}
