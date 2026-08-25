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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SignLanguage
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.ui.draw.shadow
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
import com.example.rafiq.data.local.UserPreferences
import com.example.rafiq.presentation.navigation.Screen
import com.example.rafiq.ui.theme.Cyan
import com.example.rafiq.ui.theme.DeepBlue
import com.example.rafiq.ui.theme.DeepBlueLight
import com.example.rafiq.ui.theme.ErrorRed
import com.example.rafiq.ui.theme.OnSurfaceMuted
import com.example.rafiq.ui.theme.OnSurfaceVariant
import com.example.rafiq.ui.theme.SuccessGreen
import com.example.rafiq.ui.theme.Teal
import com.example.rafiq.ui.theme.WarningAmber
import com.example.rafiq.util.HapticFeedback
import kotlinx.coroutines.launch
import java.util.Calendar

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

    if (Build.VERSION.SDK_INT >= 33) {
        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { }
        val notificationPermissionRequested = remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            if (!notificationPermissionRequested.value) {
                notificationPermissionRequested.value = true
                val granted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!granted) notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    var sessionDialogShown by remember { mutableStateOf(false) }
    LaunchedEffect(disabilityType, whatsNewVersion, disabilityPromptShown) {
        if (!sessionDialogShown) {
            if (whatsNewVersion < 1) { showWhatsNew = true; sessionDialogShown = true }
            else if (disabilityType.isEmpty() && !disabilityPromptShown) { showDisabilityDialog = true; sessionDialogShown = true }
        }
    }

    if (showWhatsNew) {
        WhatsNewDialog(onDismiss = {
            showWhatsNew = false
            scope.launch {
                userPreferences.setWhatsNewVersion(1)
                if (disabilityType.isEmpty() && !disabilityPromptShown) showDisabilityDialog = true
            }
        })
    }

    if (showDisabilityDialog) {
        DisabilityDialog(
            onSelect = { option ->
                scope.launch {
                    userPreferences.setDisabilityType(option)
                    userPreferences.setDisabilityPromptShown(true)
                    userPreferences.addPoints(50)
                }
                viewModel.syncDisabilityType(option)
                showDisabilityDialog = false
            },
            onSkip = {
                scope.launch { userPreferences.setDisabilityPromptShown(true) }
                showDisabilityDialog = false
            }
        )
    }

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    val guardianMode by userPreferences.guardianMode.collectAsState(initial = false)

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            HeroHeader(greeting = greeting, points = totalPoints, guardianMode = guardianMode, onPointsClick = {
                HapticFeedback.lightClick(context); navController.navigate(Screen.CompanionScore.route)
            })

            AIAssistantSection(
                onChatClick = { navController.navigate(Screen.Chat.route) },
                onVoiceClick = { navController.navigate(Screen.Voice.route) }
            )

            QuickActionsRow(
                onNavigate = { route ->
                    HapticFeedback.lightClick(context)
                    navController.navigate(route)
                }
            )

            FeatureCategory(
                title = "Accessibility Tools",
                items = listOf(
                    FeatureItem("Voice Assistant", "Speak to RAFIQ", Icons.Default.Mic, Cyan, Screen.Voice.route),
                    FeatureItem("Sign Language", "Gesture recognition", Icons.Default.SignLanguage, Teal, Screen.SignLanguage.route),
                    FeatureItem("Be My Eyes", "Live helper camera", Icons.Default.Visibility, Teal, Screen.BeMyEyes.route)
                ),
                onNavigate = { route -> HapticFeedback.lightClick(context); navController.navigate(route) }
            )

            FeatureCategory(
                title = "Health & Medication",
                items = listOf(
                    FeatureItem("Medication", "Daily reminders", Icons.Default.Medication, WarningAmber, Screen.Medication.route),
                    FeatureItem("Contacts", "Emergency contacts", Icons.Default.People, Cyan, Screen.Contacts.route),
                    FeatureItem("Hospital", "Nearby care", Icons.Default.LocalHospital, ErrorRed, Screen.Hospital.route)
                ),
                onNavigate = { route -> HapticFeedback.lightClick(context); navController.navigate(route) }
            )

            FeatureCategory(
                title = "Navigation & Safety",
                items = listOf(
                    FeatureItem("Map & Places", "Explore nearby", Icons.Default.LocationOn, Cyan, Screen.Map.route),
                    FeatureItem("Safety Rights", "Know your rights", Icons.Default.Gavel, WarningAmber, Screen.Awareness.route)
                ),
                onNavigate = { route -> HapticFeedback.lightClick(context); navController.navigate(route) }
            )

            FeatureCategory(
                title = "Learning & Training",
                items = listOf(
                    FeatureItem("Learn & Exercise", "Brain training", Icons.Default.School, SuccessGreen, Screen.Learning.route),
                    FeatureItem("Companion Score", "Your progress", Icons.Default.Star, WarningAmber, Screen.CompanionScore.route)
                ),
                onNavigate = { route -> HapticFeedback.lightClick(context); navController.navigate(route) }
            )

            EmergencySOSButton(
                onClick = { HapticFeedback.heavyClick(context); navController.navigate(Screen.SOS.route) }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HeroHeader(
    greeting: String,
    points: Int,
    guardianMode: Boolean,
    onPointsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepBlue, DeepBlueLight, MaterialTheme.colorScheme.background)
                )
            )
            .padding(start = 20.dp, end = 20.dp, top = 36.dp, bottom = 16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "RAFIQ",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp,
                        color = Cyan
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$greeting, Friend",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable(onClick = onPointsClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = WarningAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$points",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            if (guardianMode) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SuccessGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Guardian Mode Active",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun AIAssistantSection(
    onChatClick: () -> Unit,
    onVoiceClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "aiPress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = Cyan.copy(alpha = 0.15f))
            .alpha(scale)
            .clickable(interactionSource = interactionSource, indication = null) { onChatClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Cyan.copy(alpha = 0.12f), Teal.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(brush = Brush.linearGradient(listOf(Cyan, Teal))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "RAFIQ Assistant",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Ask me anything",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Cyan,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickActionsRow(onNavigate: (String) -> Unit) {
    val actions = listOf(
        QuickAction("Map", Icons.Default.LocationOn, Cyan, Screen.Map.route),
        QuickAction("Voice", Icons.Default.Mic, Teal, Screen.Voice.route),
        QuickAction("Sign", Icons.Default.SignLanguage, Teal, Screen.SignLanguage.route),
        QuickAction("SOS", Icons.Default.Sos, ErrorRed, Screen.SOS.route),
        QuickAction("Chat", Icons.AutoMirrored.Filled.Chat, Cyan, Screen.Chat.route)
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(actions) { action ->
            QuickActionChip(action = action, onClick = { onNavigate(action.route) })
        }
    }
}

@Composable
private fun QuickActionChip(action: QuickAction, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chipPress"
    )

    Card(
        modifier = Modifier
            .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = action.color.copy(alpha = 0.1f))
            .alpha(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(action.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    action.icon,
                    contentDescription = null,
                    tint = action.color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = action.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FeatureCategory(
    title: String,
    items: List<FeatureItem>,
    onNavigate: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceMuted,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        items.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    FeatureCard(item = item, modifier = Modifier.weight(1f), onClick = { onNavigate(item.route) })
                }
                repeat(3 - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun FeatureCard(
    item: FeatureItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "cardPress"
    )

    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(20.dp), ambientColor = item.color.copy(alpha = 0.08f))
            .alpha(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .semantics { contentDescription = "${item.title}. ${item.subtitle}. Double tap to open." },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(item.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    item.icon,
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmergencySOSButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "sos")
    val sosPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sosPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .scale(1.02f)
                .alpha(sosPulse * 0.15f)
                .clip(RoundedCornerShape(20.dp))
                .background(ErrorRed)
        )
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorRed,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .semantics { contentDescription = "Emergency SOS Button. Double tap to trigger alert." }
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "EMERGENCY SOS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun DisabilityDialog(
    onSelect: (String) -> Unit,
    onSkip: () -> Unit
) {
    var selected by remember { mutableStateOf("") }
    val options = listOf(
        "Blind / No sight", "Low vision / Partial sight", "Hearing impaired",
        "Mobility impaired", "Cognitive / Learning difficulty", "Prefer not to say"
    )

    AlertDialog(
        onDismissRequest = onSkip,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text("Tell us about yourself", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "What best describes your situation?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selected = option }
                            .background(
                                if (selected == option) Cyan.copy(alpha = 0.1f)
                                else Color.Transparent
                            )
                            .padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(if (selected == option) Cyan else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selected == option) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(option, fontWeight = if (selected == option) FontWeight.SemiBold else FontWeight.Normal)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSelect(selected) },
                enabled = selected.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) { Text("Continue", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onSkip) { Text("Skip") }
        }
    )
}

private data class QuickAction(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

data class FeatureItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)
