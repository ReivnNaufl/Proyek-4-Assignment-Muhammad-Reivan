package com.example.p4w1.ui.screen.home

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.p4w1.viewmodel.ProfileViewModel
import java.io.File
import com.example.p4w1.viewmodel.ImageViewModel
import com.example.p4w1.data.ProfileImage

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel,
    imgViewModel: ImageViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profileStateFlow.collectAsState()
    var studentName by rememberSaveable { mutableStateOf(profile?.username ?: "Muhammad Reivan Naufal Mufid") }
    var studentId by rememberSaveable { mutableStateOf(profile?.uid ?: "231511021") }
    var studentEmail by rememberSaveable { mutableStateOf(profile?.email ?: "muhammad.reivan.tif23@gmail.com") }

    val profileImage by imgViewModel.profileImage.collectAsState()
    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isEditing by rememberSaveable { mutableStateOf(false) }

    val displayedImage = remember { derivedStateOf { selectedImageUri ?: profileImage?.let { Uri.fromFile(File(it)) } } }

    LaunchedEffect(Unit) {
        imgViewModel.getProfileImage() // Load saved image
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri // Update instantly in UI
    }

    LaunchedEffect(profile) {
        profile?.let {
            studentName = it.username
            studentId = it.uid
            studentEmail = it.email
        }
    }
    Column {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
        )
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .background(Color.LightGray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (displayedImage.value != null) {
                        AsyncImage(
                            model = displayedImage.value,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(120.dp).clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Profile Picture",
                            tint = Color.Gray,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditing) {
                    // Edit mode: Display input fields to modify the student profile.
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("Student Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = { Text("Student ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = studentEmail,
                        onValueChange = { studentEmail = it },
                        label = { Text("Student Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Edit button to switch to edit mode.
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Upload Photo")
                    }

                    Button(
                        onClick = {
                            selectedImageUri?.let { uri ->
                                imgViewModel.saveImage(context, uri) // Save to Room
                                imgViewModel.getProfileImage() // Force refresh
                            }

                            val updatedData = profile!!.copy(
                                username = studentName,
                                uid = studentId,
                                email = studentEmail
                            )
                            viewModel.updateData(updatedData)

                            isEditing = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save")
                    }
                } else {
                    // Display mode: Show the student's profile details.
                    Text(
                        text = studentName,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ID: $studentId",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = studentEmail,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { isEditing = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Edit Profile")
                    }
                }
            }
        }
    }
}
