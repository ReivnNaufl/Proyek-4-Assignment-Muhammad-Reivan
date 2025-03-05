package com.example.p4w1.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.p4w1.viewmodel.DataViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(navController: NavHostController, viewModel: DataViewModel){
    val rowCount by viewModel.rowCount.observeAsState(0)

    LaunchedEffect(Unit) {  // Fetch data when Composable launches
        viewModel.fetchRowCount()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = rowCount.toString(),
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Data Inserted",
                fontSize = 30.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(26.dp))
            Text(
                text = buildAnnotatedString {
                    append("Made with ")
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                        append("tears and blood")
                    }
                    append("\tlove and care\nby Muhammad Reivan Naufal Mufid (231511021)")
                },
                fontSize = 10.sp
            )
        }
    }
}
