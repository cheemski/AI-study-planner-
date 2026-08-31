package com.example.studyplannerapp.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyplannerapp.ui.components.CircleBackButton
import com.example.studyplannerapp.ui.components.ScreenBackground
import com.example.studyplannerapp.ui.theme.InkNavy

@Composable
fun EditProfileScreen(onBack: () -> Unit, viewModel: ProfileViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf(uiState.displayName) }

    // The real name loads in asynchronously from Supabase after the ViewModel
    // is created, so sync the field once it arrives (only if untouched).
    LaunchedEffect(uiState.displayName) {
        if (name.isBlank()) name = uiState.displayName
    }

    ScreenBackground {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircleBackButton(onClick = onBack)
                Spacer(Modifier.size(16.dp))
                Text("Edit Profile", color = InkNavy, fontSize = 22.sp)
            }
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(
                "Photo upload isn't available yet — coming in a future update.",
                color = InkNavy.copy(alpha = 0.5f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            uiState.errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            uiState.infoMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = Color(0xFF2E7D32))
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.updateDisplayName(name) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Save changes")
                }
            }
        }
    }
}
