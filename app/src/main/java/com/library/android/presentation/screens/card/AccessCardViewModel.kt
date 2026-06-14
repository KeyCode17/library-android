package com.library.android.presentation.screens.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.library.android.domain.device.QrEncoder
import com.library.android.domain.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Builds the member's access card: encodes a membership reference (the principal id from
 * `/auth/me`, NOT a JWT/secret) into a QR via the [QrEncoder] port. Auth required.
 */
@HiltViewModel
class AccessCardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val qrEncoder: QrEncoder,
) : ViewModel() {

    private val _state = MutableStateFlow<AccessCardUiState>(AccessCardUiState.Loading)
    val state: StateFlow<AccessCardUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = AccessCardUiState.Loading
            authRepository.currentUser()
                .onSuccess { principal ->
                    qrEncoder.encode(MEMBERSHIP_PREFIX + principal.id)
                        .onSuccess {
                            _state.value = AccessCardUiState.Ready(principal.id, principal.email, it)
                        }
                        .onFailure { _state.value = AccessCardUiState.Error(it.message ?: DEFAULT_ERROR) }
                }
                .onFailure { _state.value = AccessCardUiState.Anonymous }
        }
    }

    private companion object {
        const val MEMBERSHIP_PREFIX = "library-member:"
        const val DEFAULT_ERROR = "Couldn't build your access card"
    }
}
