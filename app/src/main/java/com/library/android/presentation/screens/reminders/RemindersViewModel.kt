package com.library.android.presentation.screens.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.library.android.domain.repo.AuthRepository
import com.library.android.domain.repo.NotificationRepository
import com.library.android.domain.usecase.RegisterDeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Loads reminder history and registers this device's push token (best-effort) on open. Auth
 * required (anonymous users see a sign-in gate). UDF.
 */
@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val registerDevice: RegisterDeviceUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RemindersUiState(isLoading = true))
    val state: StateFlow<RemindersUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            if (authRepository.currentUser().isFailure) {
                _state.value = RemindersUiState(isAnonymous = true)
                return@launch
            }
            // Register/refresh the FCM token for the scheduler (best-effort — failures don't
            // block the screen; the app works without push).
            registerDevice()
            notificationRepository.reminders()
                .onSuccess { _state.value = RemindersUiState(reminders = it) }
                .onFailure { _state.value = RemindersUiState(error = it.message) }
        }
    }
}
