package com.example.studyplannerapp.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyplannerapp.AppNavHost.AppNavHost
import com.example.studyplannerapp.R

@Composable
fun LoginRoute(viewModel: AuthViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isCheckingSession -> SplashScreen()
        uiState.isLoggedIn -> AppNavHost(onLogout = viewModel::logout)
        else -> LoginScreen(
            uiState = uiState,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onPrimaryClick = {
                if (uiState.screen == AuthScreen.REGISTER) viewModel.register() else viewModel.login()
            },
            onToggleMode = {
                viewModel.navigateTo(
                    if (uiState.screen == AuthScreen.REGISTER) AuthScreen.LOGIN else AuthScreen.REGISTER
                )
            }
        )
    }
}

@Composable
private fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPrimaryClick: () -> Unit,
    onToggleMode: () -> Unit
) {
    val isRegister = uiState.screen == AuthScreen.REGISTER

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.White.copy(alpha = 0.75f),
        unfocusedContainerColor = Color.White.copy(alpha = 0.6f),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )

    // Real phone keyboards (Gboard, Samsung Keyboard, etc.) apply autocorrect
    // and autocapitalization by default when no KeyboardType is specified.
    // That can silently change what's actually typed even though the emulator's
    // plain AOSP keyboard usually doesn't touch it — hence "works on emulator,
    // fails on device" with the exact same credentials.
    val emailKeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Email,
        autoCorrectEnabled = false,
        capitalization = KeyboardCapitalization.None
    )

    val passwordKeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Password,
        autoCorrectEnabled = false,
        capitalization = KeyboardCapitalization.None
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.login_logo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.15f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = fieldColors,
                keyboardOptions = emailKeyboardOptions
            )

            Spacer(Modifier.height(20.dp))

            TextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = passwordKeyboardOptions
            )

            if (isRegister) {
                Spacer(Modifier.height(20.dp))
                TextField(
                    value = uiState.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = { Text("Confirm password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = passwordKeyboardOptions
                )
            }

            uiState.errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            uiState.infoMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = Color(0xFF2E7D32))
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onPrimaryClick,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Filled.Login, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isRegister) "Create account" else "Log in")
                }
            }

            Spacer(Modifier.height(24.dp))

            Row {
                Text(
                    if (isRegister) "Already have an account? " else "Don't have an account? ",
                    color = Color.White
                )
                Text(
                    if (isRegister) "Log in" else "Sign up",
                    color = Color(0xFF66B2FF),
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onToggleMode() }
                )
            }
        }
    }
}
