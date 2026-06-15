package com.library.android.presentation.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.library.android.domain.model.Role
import com.library.android.domain.model.UserSummary
import com.library.android.domain.repo.AuthRepository
import com.library.android.domain.repo.UserAdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UiState for admin Manage Users. [isForbidden] gates the screen for non-admins (client side). */
data class ManageUsersUiState(
    val isLoading: Boolean = true,
    val isForbidden: Boolean = false,
    val users: List<UserSummary> = emptyList(),
    val message: String? = null,
)

/**
 * Admin user management. Gates on the `/auth/me` role (server enforces regardless) and surfaces
 * last-admin/lockout errors via [ManageUsersUiState.message]. UDF.
 */
@HiltViewModel
class ManageUsersViewModel @Inject constructor(
    private val userAdminRepository: UserAdminRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ManageUsersUiState())
    val state: StateFlow<ManageUsersUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val me = authRepository.currentUser().getOrNull()
            if (me == null || me.role != Role.ADMIN) {
                _state.value = ManageUsersUiState(isLoading = false, isForbidden = true)
                return@launch
            }
            refresh()
        }
    }

    fun assignRole(id: String, role: Role) = mutate("Role updated") { userAdminRepository.assignRole(id, role) }

    fun createUser(email: String, password: String, role: Role) =
        mutate("User created") { userAdminRepository.createUser(email, password, role) }

    fun setActive(id: String, active: Boolean) =
        mutate(if (active) "User activated" else "User deactivated") {
            userAdminRepository.updateUser(id, email = null, active = active)
        }

    fun deleteUser(id: String) = mutate("User deleted") { userAdminRepository.deleteUser(id) }

    // Runs a mutating admin call, then refreshes the list with a success message or surfaces the
    // error. refresh() is suspend, so it's called from the coroutine body (not an onSuccess lambda).
    private fun mutate(success: String, block: suspend () -> Result<*>) {
        viewModelScope.launch {
            val result = block()
            if (result.isSuccess) {
                refresh(success)
            } else {
                _state.value = _state.value.copy(message = result.exceptionOrNull()?.message)
            }
        }
    }

    private suspend fun refresh(message: String? = null) {
        userAdminRepository.listUsers()
            .onSuccess { _state.value = ManageUsersUiState(users = it, message = message) }
            .onFailure { _state.value = _state.value.copy(isLoading = false, message = it.message) }
    }
}
