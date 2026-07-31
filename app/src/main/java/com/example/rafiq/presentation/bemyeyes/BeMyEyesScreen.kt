package com.example.rafiq.presentation.bemyeyes

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeMyEyesScreen(
    navController: NavController,
    viewModel: BeMyEyesViewModel = hiltViewModel()
) {
    val isCalling by viewModel.isCalling.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val isVideoOn by viewModel.isVideoOn.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Be My Eyes") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.endCall()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.DarkGray,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.DarkGray)
        ) {
            if (isConnected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF2C2C2C)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isVideoOn) {
                        Text(
                            "Volunteer's Camera View\n[MOCKED LIVE FEED]",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            "Camera Off",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 40.dp)
                ) {
                    Text(
                        text = when {
                            isCalling -> "Calling a Volunteer..."
                            isConnected -> "Connected to Volunteer"
                            else -> "Call Ended"
                        },
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.semantics {
                            contentDescription = when {
                                isCalling -> "Calling a volunteer"
                                isConnected -> "Connected to volunteer"
                                else -> "Call ended"
                            }
                        }
                    )

                    // Calling animation
                    if (isCalling) {
                        Spacer(modifier = Modifier.height(32.dp))
                        CallingPulseAnimation()
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.toggleMute() },
                        enabled = isConnected,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                if (isMuted) Color.White
                                else if (isConnected) Color.Gray.copy(alpha = 0.5f)
                                else Color.Gray.copy(alpha = 0.2f)
                            )
                            .semantics {
                                contentDescription =
                                    if (isMuted) "Unmute microphone" else "Mute microphone"
                            }
                    ) {
                        Icon(
                            if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = null,
                            tint = if (isMuted) Color.Black else Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.endCall()
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .semantics { contentDescription = "End Call" }
                    ) {
                        Icon(
                            Icons.Default.CallEnd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleVideo() },
                        enabled = isConnected,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                if (!isVideoOn) Color.White
                                else if (isConnected) Color.Gray.copy(alpha = 0.5f)
                                else Color.Gray.copy(alpha = 0.2f)
                            )
                            .semantics {
                                contentDescription =
                                    if (isVideoOn) "Turn off camera" else "Turn on camera"
                            }
                    ) {
                        Icon(
                            if (!isVideoOn) Icons.Default.VideocamOff else Icons.Default.Videocam,
                            contentDescription = null,
                            tint = if (!isVideoOn) Color.Black else Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CallingPulseAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "calling_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "calling_scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "calling_alpha"
    )

    Box(contentAlignment = Alignment.Center) {
        // Outer pulse ring
        Box(
            modifier = Modifier
                .size(80.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(Color.Green.copy(alpha = alpha * 0.3f))
        )
        // Inner ring
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Green.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Text("📞", fontSize = MaterialTheme.typography.headlineMedium.fontSize)
        }
    }
}
