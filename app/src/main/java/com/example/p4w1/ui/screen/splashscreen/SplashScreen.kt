package com.example.p4w1.ui.screen.splashscreen

import android.media.MediaPlayer
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.p4w1.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var rotation by remember { mutableStateOf(0f) }

    // Animate rotation from 0 to 360 degrees
    val animatedRotation by animateFloatAsState(
        targetValue = if (rotation == 0f) 360f else 0f,
        animationSpec = tween(durationMillis = 3000, easing = LinearEasing),
        label = "rotationAnimation"
    )

    LaunchedEffect(Unit) {
        mediaPlayer = MediaPlayer.create(context, R.raw.jingle).apply {
            start()
            setOnCompletionListener { mp ->
                mp.release()
                mediaPlayer = null
                onSplashFinished() // Navigate when jingle finishes
            }
        }

        // Start rotation animation
        rotation = 360f

        // Backup timeout in case jingle is too long
        delay(3000)
        onSplashFinished()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xff222233)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.offsetsun),
                    contentDescription = "Jetsnack Logo",
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                        .rotate(animatedRotation) // Apply rotation animation
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Muhammad Reivan Naufal Mufid",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xffF3F3E0)
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "231511021",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xffF3F3E0)
                )
                Spacer(Modifier.height(36.dp))
                Text(
                    text = "Proyek 4",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xffF3F3E0)
                )
            }
        }
    }
}
