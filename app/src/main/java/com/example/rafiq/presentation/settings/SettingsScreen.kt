package com.example.rafiq.presentation.settings

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.ContactEmergency
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rafiq.presentation.navigation.Screen
import com.example.rafiq.ui.components.RafiqTopBar
import com.example.rafiq.util.HapticFeedback
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val emergencyContact by settingsViewModel.emergencyContact.collectAsState()
    val darkTheme by settingsViewModel.darkTheme.collectAsState()
    val currentLanguage by settingsViewModel.language.collectAsState()
    val currentFontSize by settingsViewModel.fontSize.collectAsState()
    val currentFontFamily by settingsViewModel.fontFamily.collectAsState()
    val currentSpeechRate by settingsViewModel.speechRate.collectAsState()

    var contactInput by remember(emergencyContact) { mutableStateOf(emergencyContact) }
    var showContactSaved by remember { mutableStateOf(false) }
    var backupMessage by remember { mutableStateOf<String?>(null) }
    var restoreMessage by remember { mutableStateOf<String?>(null) }

    val logoutState by settingsViewModel.logoutState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(logoutState) {
        if (logoutState is com.example.rafiq.presentation.settings.SettingsViewModel.LogoutState.Done) {
            context.startActivity(
                context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
                    addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
            java.lang.System.exit(0)
        }
    }

    Scaffold(
        topBar = {
            RafiqTopBar(
                title = "Settings",
                subtitle = "Personalize your experience",
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Emergency Contact", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = contactInput,
                        onValueChange = { contactInput = it },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Default.ContactEmergency, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Emergency contact phone number" },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (contactInput.isNotBlank()) {
                                settingsViewModel.saveEmergencyContact(contactInput)
                                showContactSaved = true
                                HapticFeedback.lightClick(context)
                            }
                        },
                        enabled = contactInput.isNotBlank() && contactInput != emergencyContact
                    ) {
                        Text("Save Contact")
                    }
                    if (showContactSaved) {
                        Text("Contact saved", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            HorizontalDivider()

            Text("Appearance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Theme", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilterChip(
                            selected = darkTheme == "light",
                            onClick = { settingsViewModel.setDarkTheme("light"); HapticFeedback.lightClick(context) },
                            leadingIcon = { Icon(Icons.Default.LightMode, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            label = { Text("Light") }
                        )
                        FilterChip(
                            selected = darkTheme == "dark",
                            onClick = { settingsViewModel.setDarkTheme("dark"); HapticFeedback.lightClick(context) },
                            leadingIcon = { Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            label = { Text("Dark") }
                        )
                        FilterChip(
                            selected = darkTheme == "system",
                            onClick = { settingsViewModel.setDarkTheme("system"); HapticFeedback.lightClick(context) },
                            leadingIcon = { Icon(Icons.Default.SettingsSuggest, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            label = { Text("System") }
                        )
                    }
                }
            }

            HorizontalDivider()

            Text("Language", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilterChip(
                            selected = currentLanguage == "en",
                            onClick = {
                                if (currentLanguage != "en") {
                                    com.example.rafiq.util.LocaleManager.persistLanguage(context, "en")
                                    settingsViewModel.setLanguage("en"); HapticFeedback.lightClick(context)
                                    context.startActivity(context.packageManager.getLaunchIntentForPackage(context.packageName)?.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK or android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
                                    java.lang.System.exit(0)
                                }
                            },
                            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            label = { Text("English") }
                        )
                        FilterChip(
                            selected = currentLanguage == "ar",
                            onClick = {
                                if (currentLanguage != "ar") {
                                    com.example.rafiq.util.LocaleManager.persistLanguage(context, "ar")
                                    settingsViewModel.setLanguage("ar"); HapticFeedback.lightClick(context)
                                    context.startActivity(context.packageManager.getLaunchIntentForPackage(context.packageName)?.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK or android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
                                    java.lang.System.exit(0)
                                }
                            },
                            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            label = { Text("العربية") }
                        )
                        FilterChip(
                            selected = currentLanguage == "fr",
                            onClick = {
                                if (currentLanguage != "fr") {
                                    com.example.rafiq.util.LocaleManager.persistLanguage(context, "fr")
                                    settingsViewModel.setLanguage("fr"); HapticFeedback.lightClick(context)
                                    context.startActivity(context.packageManager.getLaunchIntentForPackage(context.packageName)?.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK or android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
                                    java.lang.System.exit(0)
                                }
                            },
                            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            label = { Text("Français") }
                        )
                    }
                }
            }

            HorizontalDivider()

            Text("Text & Display", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Font Size", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("small", "normal", "large", "xlarge").forEach { size ->
                            FilterChip(
                                selected = currentFontSize == size,
                                onClick = { settingsViewModel.setFontSize(size); HapticFeedback.lightClick(context) },
                                label = { Text(size.first().uppercase() + size.drop(1)) },
                                leadingIcon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                    }

                    HorizontalDivider()

                    Text("Font Style", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("default", "serif", "sans-serif", "monospace").forEach { family ->
                            FilterChip(
                                selected = currentFontFamily == family,
                                onClick = { settingsViewModel.setFontFamily(family); HapticFeedback.lightClick(context) },
                                label = { Text(family.replaceFirstChar { it.uppercase() }) },
                                leadingIcon = { Icon(Icons.Default.FontDownload, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            Text("Speech Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Speech Rate", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    val rateOptions = listOf(
                        0.5f to "Slow",
                        0.75f to "Normal",
                        1.0f to "Fast",
                        1.5f to "Faster"
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rateOptions.forEach { (rate, label) ->
                            FilterChip(
                                selected = currentSpeechRate == rate,
                                onClick = { settingsViewModel.setSpeechRate(rate); HapticFeedback.lightClick(context) },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            Text("Guardian Mode", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                val guardianMode by settingsViewModel.guardianMode.collectAsState()
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Receive Guardian Alerts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                "Get full-screen emergency alerts and siren when someone you care about triggers SOS",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Switch(
                            checked = guardianMode,
                            onCheckedChange = { settingsViewModel.setGuardianMode(it) },
                            colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }

            HorizontalDivider()

            Text("Data Backup", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Auto Backup is enabled", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = {
                                settingsViewModel.backupData { success, msg ->
                                    backupMessage = msg
                                    scope.launch { kotlinx.coroutines.delay(3000); backupMessage = null }
                                    if (success) HapticFeedback.heavyClick(context)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Backup")
                        }
                        OutlinedButton(
                            onClick = {
                                settingsViewModel.restoreData { success, msg ->
                                    restoreMessage = if (success) "Data restored successfully" else "Restore failed"
                                    scope.launch { kotlinx.coroutines.delay(3000); restoreMessage = null }
                                    if (success) HapticFeedback.heavyClick(context)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Restore")
                        }
                    }
                    backupMessage?.let { msg ->
                        Text(msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                    restoreMessage?.let { msg ->
                        Text(msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            HorizontalDivider()

            Text("Account", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            var showLogoutDialog by remember { mutableStateOf(false) }
            Button(
                onClick = { showLogoutDialog = true },
                enabled = logoutState !is com.example.rafiq.presentation.settings.SettingsViewModel.LogoutState.LoggingOut,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (logoutState is com.example.rafiq.presentation.settings.SettingsViewModel.LogoutState.LoggingOut) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onError, modifier = Modifier.size(20.dp))
                } else {
                    Text("Log Out", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    shape = RoundedCornerShape(24.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    title = {
                        Text("Log Out?", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                    },
                    text = {
                        Text(
                            "This will clear your login session. Your local data will be preserved.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showLogoutDialog = false
                                settingsViewModel.logout()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Log Out", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            val version = try {
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
            } catch (_: Exception) {
                "1.0"
            }
            Text(
                "RAFIQ v$version",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


