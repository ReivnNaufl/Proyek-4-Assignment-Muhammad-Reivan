package com.example.p4w1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.p4w1.ui.screen.splashscreen.SplashScreen
import com.example.p4w1.ui.theme.P4W1Theme
import com.example.p4w1.viewmodel.DataViewModel
import com.example.p4w1.viewmodel.ImageViewModel
import com.example.p4w1.viewmodel.ProfileViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: DataViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val imgViewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var showSplash by remember { mutableStateOf(true) }

            if (showSplash) {
                SplashScreen { showSplash = false }
            } else {
                P4W1Theme {
                    MainApp(viewModel = viewModel,
                        profileViewModel = profileViewModel,
                        imgViewModel = imgViewModel,
                        context = applicationContext
                    )
                }
            }
        }
    }
}
