package com.example.p4w1.ui.screen.data_list

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.p4w1.data.DataEntity
import com.example.p4w1.viewmodel.DataViewModel
import androidx.compose.runtime.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun DataListScreen(navController: NavHostController, viewModel: DataViewModel) {
    val pagedItems: LazyPagingItems<DataEntity> = viewModel.pagedData.collectAsLazyPagingItems()

    var showDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<DataEntity?>(null) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // Log scroll events
    LaunchedEffect(pagedItems) {
        snapshotFlow { pagedItems.loadState.append }
            .collect { loadState ->
                Log.d("DataListScreen", "Append load state: $loadState")
            }
    }


    GradientBackgroundScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (searchQuery.text.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = TextFieldValue("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                placeholder = { Text("Search by kabupaten/kota...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1A1A1A),
                    unfocusedContainerColor = Color(0xFF232323),
                    disabledContainerColor = Color(0xFF121212)
                )
            )

            // Filtered Items
            val filteredItems = pagedItems.itemSnapshotList.items.filter {
                it.namaKabupatenKota.contains(searchQuery.text, ignoreCase = true)
            }

            // Show message if no data is found
            if (filteredItems.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            ":(",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF989898),
                            textAlign = TextAlign.Center,
                            fontSize = 100.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No Data Found",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF989898),
                            textAlign = TextAlign.Center,
                            fontSize = 40.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Try inserting some..?",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF656565),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    }
                }
            } else {
                // LazyColumn for the list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        filteredItems,
                        key = { item -> item.id }
                    ) { item ->
                        if (item != null) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(16.dp)
                                ) {
                                    // Item details
                                    Text(
                                        text = "Provinsi: ${item.namaProvinsi} (${item.kodeProvinsi})",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Kabupaten/Kota: ${item.namaKabupatenKota} (${item.kodeKabupatenKota})",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Jenis Penyakit: ${item.jenisPenyakit.displayName}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Total: ${item.total} ${item.satuan.displayName}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tahun: ${item.tahun}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Edit Button
                                        Button(
                                            onClick = { navController.navigate("edit/${item.id}") },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "Edit")
                                        }
                                        Spacer(modifier = Modifier.width(5.dp))
                                        // Delete Button
                                        Button(
                                            onClick = {
                                                itemToDelete = item
                                                showDialog = true
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFFF6B6B)
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                modifier = Modifier.size(18.dp),
                                                tint = Color(0xFFE8E8E8)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Delete",
                                                color = Color(0xFFE8E8E8)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Data Deletion") },
            text = { Text("Are you sure you want to delete this item? This action cannot be reversed.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemToDelete?.let { viewModel.deleteData(it) }
                        showDialog = false
                    }
                ) {
                    Text("Delete", color = Color(0xFFFF6B6B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Composable
fun GradientBackgroundScreen(content: @Composable () -> Unit) {
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
            lerp(Color(0xFF10071A), Color(0xFF060E1A), colorPhase), // Near-Black Purple to Near-Black Blue
            lerp(Color(0xFF060E1A), Color(0xFF20060A), colorPhase), // Near-Black Blue to Deep Black-Red
            lerp(Color(0xFF20060A), Color(0xFF10071A), colorPhase)  // Deep Black-Red to Near-Black Purple
        )
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush) // Apply animated gradient
            .padding(0.dp)
    ) {
        content()
    }
}