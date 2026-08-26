package com.example.authdemo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authdemo.data.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Which screen is currently shown
enum class AuthScreen { LOGIN, REGISTER, FORGOT_PASSWORD, RESET_PASSWORD }

data class AuthUiState(
    val screen: AuthScreen = AuthScreen.LOGIN,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,   // e.g. "Check your email"
    val isLoggedIn: Boolean = false,
    // True while the SDK is still reading a saved session from disk on launch.
    // Show a splash/loading state instead of the login screen while this is true.
    val isCheckingSession: Boolean = true
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Supabase persists the session to disk automatically after any sign-in.
        // On launch it re-reads that saved session and, if the access token is
        // expired, silently refreshes it — this stream tells us the result.
        viewModelScope.launch {
            SupabaseClient.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> _uiState.update {
                        it.copy(isLoggedIn = true, isCheckingSession = false)
                    }
                    is SessionStatus.NotAuthenticated -> _uiState.update {
                        it.copy(isLoggedIn = false, isCheckingSession = false)
                    }
                    is SessionStatus.RefreshFailure -> _uiState.update {
                        // Had a saved session but couldn't refresh it (e.g. offline,
                        // or the refresh token was revoked) — fall back to login.
                        it.copy(
                            isLoggedIn = false,
                            isCheckingSession = false,
                            errorMessage = "Session expired, please log in again"
                        )
                    }
                    is SessionStatus.Initializing -> _uiState.update {
                        it.copy(isCheckingSession = true)
                    }
                }
            }
        }
    }

    fun logout() = viewModelScope.launch {
        SupabaseClient.auth.signOut()
        // sessionStatus will emit NotAuthenticated automatically after this,
        // which updates isLoggedIn via the collector above.
    }

    // ---------- Field updates ----------
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }

    fun navigateTo(screen: AuthScreen) =
        _uiState.update { it.copy(screen = screen, errorMessage = null, infoMessage = null) }

    // ---------- Login ----------
    fun login() = viewModelScope.launch {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email and password are required") }
            return@launch
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            SupabaseClient.auth.signInWith(Email) {
                email = state.email
                password = state.password
            }
            _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Login failed") }
        }
    }

    // ---------- Register ----------
    fun register() = viewModelScope.launch {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email and password are required") }
            return@launch
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return@launch
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return@launch
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            SupabaseClient.auth.signUpWith(Email) {
                email = state.email
                password = state.password
            }
            // Depending on your Supabase project settings, email confirmation
            // may be required before the session becomes active.
            _uiState.update {
                it.copy(
                    isLoading = false,
                    infoMessage = "Account created. Check your email to confirm, then log in.",
                    screen = AuthScreen.LOGIN
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Registration failed") }
        }
    }

    // ---------- Forgot password: send reset email ----------
    fun sendPasswordReset() = viewModelScope.launch {
        val state = _uiState.value
        if (state.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter your email first") }
            return@launch
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            SupabaseClient.auth.resetPasswordForEmail(state.email)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    infoMessage = "Reset link sent. Check your email.",
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Could not send reset email") }
        }
    }

    // ---------- Reset password: called after the deep link brings back a recovery session ----------
    fun updatePasswordAfterRecovery() = viewModelScope.launch {
        val state = _uiState.value
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return@launch
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return@launch
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            SupabaseClient.auth.updateUser {
                password = state.password
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    infoMessage = "Password updated. Please log in.",
                    screen = AuthScreen.LOGIN,
                    password = "",
                    confirmPassword = ""
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Could not update password") }
        }
    }

    // Called from MainActivity when the app is opened via the reset-password deep link.
    fun onRecoveryDeepLinkReceived() {
        _uiState.update { it.copy(screen = AuthScreen.RESET_PASSWORD, errorMessage = null, infoMessage = null) }
    }
}

// Small helper so MutableStateFlow.update reads cleanly above
private fun <T> MutableStateFlow<T>.update(transform: (T) -> T) {
    value = transform(value)
}
