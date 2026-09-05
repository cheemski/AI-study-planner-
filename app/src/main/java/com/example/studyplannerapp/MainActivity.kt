package com.example.studyplannerapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studyplannerapp.data.SettingsDataStore
import com.example.studyplannerapp.ui.login.AuthViewModel
import com.example.studyplannerapp.ui.login.LoginRoute
import com.example.studyplannerapp.ui.theme.StudyPlannerAppTheme

// Matches the <data android:scheme="studyplanner" android:host="reset-callback" />
// intent-filter declared for this activity in AndroidManifest.xml.
private const val RESET_PASSWORD_SCHEME = "studyplanner"
private const val RESET_PASSWORD_HOST = "reset-callback"

class MainActivity : ComponentActivity() {

    // Hoisted here (instead of left to LoginRoute's default viewModel()) so this
    // activity can push the password-reset deep link into it directly.
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
        setContent {
            val context = LocalContext.current
            val darkMode by SettingsDataStore.darkModeFlow(context).collectAsStateWithLifecycle(initialValue = false)
            StudyPlannerAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    LoginRoute(viewModel = authViewModel)
                }
            }
        }
    }

    // With launchMode="singleTask", re-tapping the reset link while the app is
    // already running delivers here instead of creating a new activity instance.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val data: Uri = intent?.data ?: return
        if (data.scheme == RESET_PASSWORD_SCHEME && data.host == RESET_PASSWORD_HOST) {
            authViewModel.onRecoveryDeepLinkReceived()
        }
    }
}