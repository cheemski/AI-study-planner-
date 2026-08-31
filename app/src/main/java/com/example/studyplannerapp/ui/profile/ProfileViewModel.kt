package com.example.studyplannerapp.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyplannerapp.data.SettingsDataStore
import com.example.studyplannerapp.network.SupabaseClient
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

data class ProfileUiState(
    val email: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val darkMode: StateFlow<Boolean> =
        SettingsDataStore.darkModeFlow(application)
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val notificationsEnabled: StateFlow<Boolean> =
        SettingsDataStore.notificationsFlow(application)
            .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        val user = SupabaseClient.auth.currentUserOrNull()
        val metadataName = user?.userMetadata?.get("full_name")?.jsonPrimitive?.content
        _uiState.update {
            it.copy(
                email = user?.email ?: "",
                displayName = metadataName ?: user?.email?.substringBefore("@").orEmpty()
            )
        }
    }

    fun setDarkMode(enabled: Boolean) = viewModelScope.launch {
        SettingsDataStore.setDarkMode(getApplication(), enabled)
    }

    fun setNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        SettingsDataStore.setNotifications(getApplication(), enabled)
    }

    // NOTE: `data` on UserUpdateBuilder is best-effort based on the typical
    // supabase-kt Auth API shape (mirrors email/password as a direct property).
    // If your installed version exposes this differently (e.g. a `data { }`
    // builder block instead of a property), adjust this one call accordingly —
    // everything else in this file doesn't depend on it.
    fun updateDisplayName(name: String) = viewModelScope.launch {
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Name can't be empty") }
            return@launch
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
        try {
            SupabaseClient.auth.updateUser {
                data = buildJsonObject { put("full_name", name) }
            }
            _uiState.update { it.copy(isLoading = false, displayName = name, infoMessage = "Profile updated") }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Could not update profile") }
        }
    }

    // NOTE: supabase-kt's Auth plugin has no dedicated "verify current password"
    // call, so the current password is checked by re-authenticating with it via
    // signInWith(Email). If that succeeds, the password is correct and we proceed
    // to updateUser. If your installed version exposes email/password sign-in
    // differently (e.g. a different provider object or parameter names), adjust
    // just this one block — everything else in this file doesn't depend on it.
    fun updatePassword(currentPassword: String, newPassword: String, confirmPassword: String) = viewModelScope.launch {
        if (currentPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter your current password") }
            return@launch
        }
        if (newPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return@launch
        }
        if (newPassword != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return@launch
        }
        if (newPassword == currentPassword) {
            _uiState.update { it.copy(errorMessage = "New password must be different from current password") }
            return@launch
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }

        val email = SupabaseClient.auth.currentUserOrNull()?.email
        if (email.isNullOrBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Could not verify current user") }
            return@launch
        }

        try {
            // Re-authenticate with the current password to confirm it's correct
            // before allowing the change.
            SupabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = currentPassword
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Current password is incorrect") }
            return@launch
        }

        try {
            SupabaseClient.auth.updateUser {
                password = newPassword
            }
            _uiState.update { it.copy(isLoading = false, infoMessage = "Password updated successfully") }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Could not update password") }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
}