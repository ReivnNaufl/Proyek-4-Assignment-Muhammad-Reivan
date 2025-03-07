package com.example.p4w1

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.p4w1.ui.navigation.*
import com.example.p4w1.ui.screen.*
import com.example.p4w1.ui.screen.data_entry.DataEntryScreen
import com.example.p4w1.ui.screen.data_list.DataListScreen
import com.example.p4w1.ui.screen.edit.EditScreen
import com.example.p4w1.ui.screen.home.HomeScreen
import com.example.p4w1.ui.screen.home.ProfileScreen
import com.example.p4w1.viewmodel.DataViewModel
import com.example.p4w1.viewmodel.ProfileViewModel
import com.example.p4w1.viewmodel.ImageViewModel

@Composable
fun MainApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: DataViewModel,
    profileViewModel: ProfileViewModel,
    imgViewModel: ImageViewModel,
    context: Context
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Screen.DataEntry.route) {
                DataEntryScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.DataList.route) {
                DataListScreen(navController = navController, viewModel = viewModel)
            }
            composable(
                route = "edit/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                EditScreen(navController = navController, viewModel = viewModel, dataId = id)
            }
            composable(Screen.Home.route){
                HomeScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Profile.route){
                ProfileScreen(navController = navController, viewModel = profileViewModel, imgViewModel = imgViewModel)
            }
        }
    }
}


@Composable
public fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Moving Gradient Effect using animated colors
    val infiniteTransition = rememberInfiniteTransition()
    val colorPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

// Interpolate colors for smooth blending
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            lerp(Color(0xFFA67CF2), Color(0xFF5C98F2), colorPhase),
            lerp(Color(0xFF5C98F2), Color(0xFFFF6B8B), colorPhase),
        )
    )
    NavigationBar(
        modifier = modifier,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val navigationItems = listOf(
            NavigationItem(
                title = "Home",
                icon = Icons.Default.Home,
                screen = Screen.Home
            ),
            NavigationItem(
                title = "Data Entry",
                icon = Icons.Default.Add,
                screen = Screen.DataEntry
            ),
            NavigationItem(
                title = "Data List",
                icon = Icons.Default.List,
                screen = Screen.DataList
            ),
            NavigationItem(
                title = "Profile",
                icon = Icons.Default.AccountCircle,
                screen = Screen.Profile
            )
        )
        navigationItems.map { item ->
            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(32.dp))
                            .width(40.dp)
                            .size(24.dp) // Adjust size of the icon
                            .graphicsLayer {
                                // You can apply transformations like rotation or scale here
                                scaleX = 1f
                                scaleY = 1f
                            }
                            .drawBehind {
                                // Apply the gradient as a background behind the icon
                                drawRect(
                                    brush = gradientBrush,
                                    size = size
                                )
                            }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.fillMaxSize(),
                            tint = Color(0xFF242730)
                        )
                    }
                },
                label = {
                    Text(
                        item.title,
                        style = TextStyle(brush = gradientBrush)
                    )
                },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
