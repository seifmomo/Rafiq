package com.example.rafiq.presentation.voice

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rafiq.ui.components.RafiqTopBar
import com.example.rafiq.ui.theme.NeonOrange
import com.example.rafiq.ui.theme.NeonOrangeLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceScreen(
    navController: NavController,
    viewModel: VoiceViewModel = hiltViewModel()
) {
    val spokenText by viewModel.spokenText.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.toggleListening()
        }
    }

    Scaffold(
        topBar = {
            RafiqTopBar(
                title = "Voice & Sign Language",
                subtitle = "Speak to RAFIQ",
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SignLanguageAvatar(isActive = isListening)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (spokenText.isEmpty() && !isListening) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Tap the mic to start speaking",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "I'll listen and respond",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Text(
                            text = spokenText,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (isListening) {
                Text(
                    text = "Listening...",
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonOrange,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = {
                    if (!isListening) {
                        val permission = Manifest.permission.RECORD_AUDIO
                        if (ContextCompat.checkSelfPermission(
                                context,
                                permission
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            viewModel.toggleListening()
                        } else {
                            permissionLauncher.launch(permission)
                        }
                    } else {
                        viewModel.toggleListening()
                    }
                },
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(
                        if (isListening) {
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.error,
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(NeonOrange, NeonOrangeLight)
                            )
                        }
                    )
                    .semantics {
                        contentDescription =
                            if (isListening) "Stop listening" else "Start Voice Input"
                    }
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Mic else Icons.Default.Mic,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }
        }
    }
}

@Composable
private fun SignLanguageAvatar(isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val orbitRadiusDp = 60.dp
    val orbitRadiusPx = with(LocalDensity.current) { orbitRadiusDp.toPx() }

    val activeGradient = listOf(
        NeonOrange.copy(alpha = 0.3f),
        NeonOrangeLight.copy(alpha = 0.1f)
    )
    val idleGradient = listOf(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = if (isActive) activeGradient else idleGradient
                )
            )
            .semantics { contentDescription = "Sign language avatar visualization" },
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\uD83E\uDD32",
                    fontSize = MaterialTheme.typography.displayLarge.fontSize,
                    modifier = Modifier.semantics {
                        contentDescription = "Sign language interpretation active"
                    }
                )
            }
            for (i in 0..2) {
                val angle = i * 120f
                val orbitRotation by infiniteTransition.animateFloat(
                    initialValue = angle,
                    targetValue = angle + 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "orbit_$i"
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .graphicsLayer {
                            translationX =
                                orbitRadiusPx * kotlin.math.cos(Math.toRadians(orbitRotation.toDouble()))
                                    .toFloat()
                            translationY =
                                orbitRadiusPx * kotlin.math.sin(Math.toRadians(orbitRotation.toDouble()))
                                    .toFloat()
                        }
                        .clip(CircleShape)
                        .background(NeonOrange.copy(alpha = 0.6f))
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "\uD83E\uDD32",
                    fontSize = MaterialTheme.typography.displayLarge.fontSize,
                    modifier = Modifier.semantics {
                        contentDescription = "Sign language avatar"
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sign Language Avatar",
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
