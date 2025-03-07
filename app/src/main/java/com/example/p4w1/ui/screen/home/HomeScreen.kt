package com.example.p4w1.ui.screen.home

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.p4w1.viewmodel.DataViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: DataViewModel) {
    val dataList by viewModel.dataList.observeAsState(initial = emptyList())
    val context = LocalContext.current
    val rowCount by viewModel.rowCount.observeAsState(0)
    val isLoading by viewModel.isLoading.observeAsState(false)

    // Counter Animation (Smoothly animates from 0 to rowCount)
    val animatedRowCount = remember { Animatable(0f) }

    // Launch animation only when rowCount changes and is greater than 0
    LaunchedEffect(rowCount) {
        viewModel.fetchRowCount()
        if (rowCount > 0) {
            animatedRowCount.snapTo(0f) // Ensure it starts from 0
            animatedRowCount.animateTo(
                targetValue = rowCount.toFloat(),
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )
        }
    }

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
            lerp(Color(0xFFA67CF2), Color(0xFF5C98F2), colorPhase), // Purple to Blue
            lerp(Color(0xFF5C98F2), Color(0xFFFF6B8B), colorPhase), // Blue to Red
            lerp(Color(0xFFFF6B8B), Color(0xFFA67CF2), colorPhase)  // Red to Purple
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Gradient Text
            Text(
                text = animatedRowCount.value.toInt().toString(),
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(brush = gradientBrush)
            )

            Text(
                text = "Data Inserted",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = buildAnnotatedString {
                    append("\t\t\tMade with ")
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                        append("tears and blood")
                    }
                    append(" love and care\n\tby Muhammad Reivan Naufal Mufid (231511021)")
                },
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Sync Button with Loading Indicator
            Button(
                onClick = { viewModel.fetchDataAndInsert() },
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Syncing...")
                } else {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Sync Icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Sync Data With OpenData Jabar")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val csvContent = viewModel.convertToCsv(dataList.reversed())
                    viewModel.saveCsvToDownloads(context, csvContent)

                    Toast.makeText(
                        context,
                        "Data successfully exported!",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ){
                Icon(imageVector = Icons.Default.Send, contentDescription = "Sync Icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export To CSV")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.exportToExcel(context, dataList.reversed())
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ){
                Icon(imageVector = Icons.Default.Send, contentDescription = "Sync Icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export To XLSX")
            }
        }
    }
}