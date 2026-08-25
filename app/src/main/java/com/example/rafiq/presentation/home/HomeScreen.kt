package com.example.rafiq.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignLanguage
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rafiq.presentation.navigation.Screen
import com.example.rafiq.ui.theme.DeepNavy
import com.example.rafiq.ui.theme.ErrorRed
import com.example.rafiq.data.local.UserPreferences
import com.example.rafiq.ui.theme.NeonOrange
import com.example.rafiq.ui.theme.NeonOrangeLight
import com.example.rafiq.ui.theme.SuccessGreen

import com.example.rafiq.util.HapticFeedback
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val totalPoints by viewModel.totalPoints.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferences = remember { UserPreferences(context) }
    val whatsNewVersion by userPreferences.whatsNewVersion.collectAsState(initial = 999)
    var showWhatsNew by remember { mutableStateOf(false) }
    val disabilityType by userPreferences.disabilityType.collectAsState(initial = "")
    val disabilityPromptShown by userPreferences.disabilityPromptShown.collectAsState(initial = false)
    var showDisabilityDialog by remember { mutableStateOf(false) }

    // Request notification permission on Android 13+ so SOS/medication alerts can reach the user
    if (Build.VERSION.SDK_INT >= 33) {
        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { }
        val notificationPermissionRequested = remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            if (!notificationPermissionRequested.value) {
                notificationPermissionRequested.value = true
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!granted) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    // Use a session-based state to ensure it only shows once per app session if version is low
    var sessionDialogShown by remember { mutableStateOf(false) }

    LaunchedEffect(disabilityType, whatsNewVersion, disabilityPromptShown) {
        if (!sessionDialogShown) {
            if (whatsNewVersion < 1) {
                showWhatsNew = true
                sessionDialogShown = true
            } else if (disabilityType.isEmpty() && !disabilityPromptShown) {
                showDisabilityDialog = true
                sessionDialogShown = true
            }
        }
    }

    if (showWhatsNew) {
        WhatsNewDialog(
            onDismiss = {
                showWhatsNew = false
                scope.launch {
                    userPreferences.setWhatsNewVersion(1)
                    if (disabilityType.isEmpty() && !disabilityPromptShown) {
                        showDisabilityDialog = true
                    }
                }
            }
        )
    }

    if (showDisabilityDialog) {
        var selectedDisability by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = {
                scope.launch { userPreferences.setDisabilityPromptShown(true) }
                showDisabilityDialog = false
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "Tell us about yourself",
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "What best describes your situation? This helps us tailor RAFIQ for you.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    listOf("Blind / No sight", "Low vision / Partial sight", "Hearing impaired", "Mobility impaired", "Cognitive / Learning difficulty", "Prefer not to say").forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedDisability = option }
                                .background(
                                    if (selectedDisability == option) NeonOrange.copy(alpha = 0.15f)
                                    else Color.Transparent
                                )
                                .padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selectedDisability == option) NeonOrange
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedDisability == option) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                option,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (selectedDisability == option) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            userPreferences.setDisabilityType(selectedDisability)
                            userPreferences.setDisabilityPromptShown(true)
                            userPreferences.addPoints(50)
                        }
                        viewModel.syncDisabilityType(selectedDisability)
                        showDisabilityDialog = false
                    },
                    enabled = selectedDisability.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonOrange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        scope.launch { userPreferences.setDisabilityPromptShown(true) }
                        showDisabilityDialog = false
                    }
                ) {
                    Text("Skip")
                }
            }
        )
    }

    val calendar = java.util.Calendar.getInstance()
    val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11 -> "Good morning, Friend"
        in 12..16 -> "Good afternoon, Friend"
        else -> "Good evening, Friend"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "luxury")

    val sosPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sosPulse"
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(DeepNavy, Color(0xFF0D2040), Color(0xFF0066CC).copy(alpha = 0.4f), NeonOrange.copy(alpha = 0.3f)),
                            start = Offset(0f, 0f),
                            end = Offset(800f, 400f)
                        )
                    )
                    .padding(start = 24.dp, end = 16.dp, top = 28.dp, bottom = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "RAFIQ",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 8.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = greeting,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(30.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            NeonOrange.copy(alpha = 0.3f),
                                            NeonOrange.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                                .clickable {
                                    HapticFeedback.lightClick(context)
                                    navController.navigate(Screen.CompanionScore.route)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "$totalPoints",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "I'm here to support you. Choose a helper tool below!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.45f),
                            modifier = Modifier.weight(1f)
                        )
                        val guardianMode by userPreferences.guardianMode.collectAsState(initial = false)
                        if (guardianMode) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SuccessGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Guardian",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Helper Tools",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                val tools = listOf(
                    ToolItem("Map & Places", "Explore nearby", Icons.Default.LocationOn, listOf(Color(0xFF3B82F6), Color(0xFF60A5FA)), Screen.Map.route),
                    ToolItem("Voice Assistant", "Speak to RAFIQ", Icons.Default.Mic, listOf(Color(0xFF0066CC), Color(0xFF00E5FF)), Screen.Voice.route),
                    ToolItem("Sign Language", "Gesture recognition", Icons.Default.SignLanguage, listOf(Color(0xFF7C3AED), Color(0xFFA78BFA)), Screen.SignLanguage.route),
                    ToolItem("Learn & Exercise", "Brain + daily training", Icons.Default.FitnessCenter, listOf(Color(0xFF10B981), Color(0xFF34D399)), Screen.Learning.route),
                    ToolItem("Safety Rights", "Know your rights", Icons.Default.Gavel, listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)), Screen.Awareness.route),
                    ToolItem("Hospital Finder", "Nearby medical care", Icons.Default.LocalHospital, listOf(Color(0xFFEF4444), Color(0xFFF87171)), Screen.Hospital.route),
                    ToolItem("Contacts", "Emergency contacts", Icons.Default.People, listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA)), Screen.Contacts.route),
                    ToolItem("Medication", "Daily reminders", Icons.Default.Medication, listOf(Color(0xFFEC4899), Color(0xFFF472B6)), Screen.Medication.route),
                    ToolItem("Chat", "Ask the assistant", Icons.AutoMirrored.Filled.Chat, listOf(Color(0xFF14B8A6), Color(0xFF2DD4BF)), Screen.Chat.route),
                    ToolItem("Be My Eyes", "Live helper camera", Icons.Default.Visibility, listOf(Color(0xFF06B6D4), Color(0xFF22D3EE)), Screen.BeMyEyes.route),
                    ToolItem("Settings", "Personalize RAFIQ", Icons.Default.Settings, listOf(Color(0xFF6366F1), Color(0xFF818CF8)), Screen.Settings.route)
                )

                tools.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { item ->
                            ToolTile(
                                item = item,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    HapticFeedback.lightClick(context)
                                    navController.navigate(item.route)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .scale(1.04f)
                            .alpha(sosPulse * 0.2f)
                            .background(ErrorRed, CircleShape)
                    )
                    Button(
                        onClick = {
                            HapticFeedback.heavyClick(context)
                            navController.navigate(Screen.SOS.route)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorRed,
                            contentColor = Color.White
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .semantics {
                                contentDescription =
                                    "Emergency SOS Button. Double tap to trigger alert."
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "EMERGENCY SOS",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                var showNewUserDialog by remember { mutableStateOf(false) }

                TextButton(
                    onClick = { showNewUserDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        "New User? Start Fresh",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        letterSpacing = 0.5.sp
                    )
                }

                if (showNewUserDialog) {
                    AlertDialog(
                        onDismissRequest = { showNewUserDialog = false },
                        shape = RoundedCornerShape(24.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        title = {
                            Text(
                                "Reset for New User?",
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        text = {
                            Text(
                                "This will restart the intro walkthrough and clear the What's New prompt. Your points and places will be preserved.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showNewUserDialog = false
                                    scope.launch {
                                        userPreferences.setIntroCompleted(false)
                                        userPreferences.setWhatsNewVersion(0)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonOrange,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Reset", fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showNewUserDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private data class ToolItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val route: String
)

@Composable
private fun ToolTile(
    item: ToolItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "toolPress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .semantics {
                contentDescription = "${item.title}. ${item.subtitle}. Double tap to open."
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.linearGradient(colors = item.gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
