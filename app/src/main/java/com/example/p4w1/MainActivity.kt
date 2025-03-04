package com.example.p4w1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.p4w1.ui.navigation.AppNavHost
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
        setContent {
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
