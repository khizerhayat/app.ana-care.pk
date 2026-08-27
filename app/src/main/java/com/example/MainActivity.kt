package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.MainPortalScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AuthState
import com.example.ui.viewmodel.PortalViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PortalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authState by viewModel.authState.collectAsState()

                    Crossfade(
                        targetState = authState,
                        animationSpec = tween(400),
                        label = "auth_screen_crossfade"
                    ) { state ->
                        when (state) {
                            is AuthState.Splash -> {
                                SplashScreen(
                                    onSplashComplete = {
                                        viewModel.setAuthState(AuthState.Authenticated)
                                    }
                                )
                            }
                            is AuthState.Login,
                            is AuthState.SignUp,
                            is AuthState.EmailVerification,
                            is AuthState.ProfileSetup -> {
                                AuthScreen(
                                    viewModel = viewModel,
                                    authState = state
                                )
                            }
                            is AuthState.Authenticated -> {
                                MainPortalScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
