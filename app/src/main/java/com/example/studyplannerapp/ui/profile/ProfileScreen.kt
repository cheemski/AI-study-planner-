package com.example.studyplannerapp.ui.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyplannerapp.ui.components.CircleBackButton
import com.example.studyplannerapp.ui.components.ScreenBackground
import com.example.studyplannerapp.ui.theme.AccentRed
import com.example.studyplannerapp.ui.theme.BrandPurple
import com.example.studyplannerapp.ui.theme.CardWhite
import com.example.studyplannerapp.ui.theme.InkMuted
import com.example.studyplannerapp.ui.theme.InkNavy
import com.example.studyplannerapp.ui.theme.RedTint

// Each settings row now carries its own icon and click action.
private data class SettingsItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleBackButton(onClick = onBack)
                    Spacer(Modifier.size(16.dp))
                    Text("My Profile", color = InkNavy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
            item { ProfileHeader(name = uiState.displayName, email = uiState.email) }
            item { Spacer(Modifier.height(20.dp)) }

            item { SettingsGroup("ACCOUNT") }
            item {
                SettingsCard(
                    listOf(
                        SettingsItem("Edit Profile", "Update your name", Icons.Filled.Person, onEditProfile),
                        SettingsItem("Change Password", "Update your password", Icons.Filled.Lock, onChangePassword)
                    )
                )
                Spacer(Modifier.height(20.dp))
            }

            item { SettingsGroup("ABOUT") }
            item {
                SettingsCard(
                    listOf(
                        SettingsItem("Privacy Policy", "How we protect your data", Icons.Filled.PrivacyTip, onPrivacyPolicy)
                    )
                )
                Spacer(Modifier.height(24.dp))
            }

            item {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = RedTint),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed)
                ) {
                    Text("Log Out", color = AccentRed, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(name: String, email: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(
            Modifier.size(110.dp).clip(RoundedCornerShape(28.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF9B2FE0), Color(0xFF6B49EC)))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                color = Color.White, fontSize = 44.sp, fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(name.ifBlank { "Your Name" }, color = InkNavy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(email, color = BrandPurple, fontSize = 15.sp)
    }
}

@Composable
private fun SettingsGroup(title: String) {
    Text(title, color = InkMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 10.dp))
}

@Composable
private fun SettingsCard(rows: List<SettingsItem>) {
    Surface(shape = RoundedCornerShape(18.dp), color = CardWhite, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column {
            rows.forEachIndexed { i, item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { item.onClick() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconTile(icon = item.icon)
                    Spacer(Modifier.size(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(item.title, color = InkNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(item.subtitle, color = InkMuted, fontSize = 13.sp, maxLines = 1)
                    }
                    Text("›", color = InkMuted, fontSize = 22.sp)
                }
                if (i < rows.size - 1) Divider(color = Color(0xFFF0EEF8), thickness = 1.dp)
            }
        }
    }
}

@Composable
private fun IconTile(icon: ImageVector) {
    Box(
        Modifier.size(40.dp).clip(RoundedCornerShape(50)).background(Color(0xFFEDE9FF)),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = BrandPurple, modifier = Modifier.size(20.dp))
    }
}