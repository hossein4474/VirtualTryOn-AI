package com.virtualtryonai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.virtualtryonai.app.ui.screens.CameraScreen
import com.virtualtryonai.app.ui.screens.EditScreen
import com.virtualtryonai.app.ui.screens.HomeScreen
import com.virtualtryonai.app.ui.theme.VirtualTryOnTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VirtualTryOnTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(navController)
                        }
                        composable("camera") {
                            CameraScreen(navController)
                        }
                        composable("edit") {
                            EditScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
