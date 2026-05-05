package com.bike.rent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bike.rent.ui.theme.BikeRentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BikeRentTheme {
                val context = LocalContext.current
                val sessionManager = remember { SessionManager(context) }
                val userHash by sessionManager.userHash.collectAsState(initial = "")
                
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Loading) }

                LaunchedEffect(userHash) {
                    if (userHash == null) {
                        currentScreen = Screen.Login
                    } else if (userHash!!.isNotEmpty()) {
                        currentScreen = Screen.Home
                    } else if (userHash == "") {
                        // Initial state, wait for collect
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                        when (currentScreen) {
                            Screen.Loading -> CircularProgressIndicator()
                            Screen.Login -> LoginScreen(onLoginSuccess = { currentScreen = Screen.Home })
                            Screen.Home -> HomeScreen()
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen {
    object Loading : Screen()
    object Login : Screen()
    object Home : Screen()
}
