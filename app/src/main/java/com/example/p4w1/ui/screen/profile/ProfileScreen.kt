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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.p4w1.viewmodel.ProfileViewModel
import java.io.File
import com.example.p4w1.viewmodel.ImageViewModel
import com.example.p4w1.data.ProfileImage

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel,
    imgViewModel: ImageViewModel,
    context: Context,
    modifier: Modifier = Modifier) {
    var isEditing by remember { mutableStateOf(false) }
    val profile by viewModel.profileStateFlow.collectAsState()

    var studentName by rememberSaveable { mutableStateOf(profile?.username ?: "Muhammad Reivan Naufal Mufid") }
    var studentId by rememberSaveable { mutableStateOf(profile?.uid ?: "231511021") }
    var studentEmail by rememberSaveable { mutableStateOf(profile?.email ?: "muhammad.reivan.tif23@gmail.com") }
    var profileUploaded by remember { mutableStateOf(false) }

    var profileImage by remember { mutableStateOf<ProfileImage?>(null) }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imgViewModel.updateProfileImage(context, it) // Save image & update Room
            imgViewModel.getProfileImage(context) { updatedImage ->
                profileImage = updatedImage // Update UI
            }
        }
    }

    // Update fields when profile changes
    LaunchedEffect(profile) {
        profile?.let {
            studentName = it.username
            studentId = it.uid
            studentEmail = it.email
        }
    }

    LaunchedEffect(Unit) {
        imgViewModel.getProfileImage(context) { profileImage = it }
    }

    if (profile == null) {
        Text("Loading profile...", modifier = Modifier.padding(16.dp))
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Profile Image Section (simulated upload)
            ProfileImage(profileImage?.imgURI)
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
                // Simulated upload button for the profile photo.
                Button(
                    onClick = { imagePickerLauncher.launch("image/*")},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Upload Photo")
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Save button to exit edit mode.
                Button(
                    onClick = {
                        val updatedData = profile!!.copy(username = studentName, uid = studentId, email = studentEmail)

                        isEditing = false
                        viewModel.updateData(updatedData)
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
                // Edit button to switch to edit mode.
                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Profile")
                }
            }
        }
    }
}

@Composable
fun ProfileImage(imgURI: String?) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            .background(Color.LightGray, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (imgURI.isNullOrEmpty()) {
            // Default icon if no image is set
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Default Profile Picture",
                tint = Color.Gray,
                modifier = Modifier.size(80.dp)
            )
        } else {
            AsyncImage(
                model = File(imgURI), // Load saved image from storage
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape) // Crop to a circle
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape), // Add border
                contentScale = ContentScale.Crop // Scale and center the image
            )
        }
    }
}

