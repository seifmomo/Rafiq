package com.example.rafiq.presentation.fall

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SensorOccupied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.rafiq.safety.FallSensorService
import com.example.rafiq.ui.components.RafiqTopBar
import com.example.rafiq.ui.theme.ErrorRed
import com.example.rafiq.ui.theme.OnSurfaceVariant
import com.example.rafiq.ui.theme.SuccessGreen
import com.example.rafiq.ui.theme.WarningAmber
import com.example.rafiq.util.HapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FallDetectionScreen(navController: NavController) {
    val context = LocalContext.current
    val sensorAvailable = remember { hasAccelerometer(context) }
    var enabled by remember { mutableStateOf(isServiceRunning(context, FallSensorService::class.java)) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    Scaffold(
        topBar = {
            RafiqTopBar(
                title = "Fall Detection",
                subtitle = "Automatic SOS when you fall",
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.SensorOccupied,
                        contentDescription = null,
                        tint = if (enabled) SuccessGreen else WarningAmber,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (enabled) "Fall detection active" else "Fall detection off",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (enabled) SuccessGreen else OnSurfaceVariant
                        )
                        Text(
                            text = if (enabled) "Monitoring your accelerometer for falls" else "Enable to watch for falls automatically",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                    Switch(
                        checked = enabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                if (Build.VERSION.SDK_INT >= 33) {
                                    val granted = ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (!granted) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                                HapticFeedback.heavyClick(context)
                                ContextCompat.startForegroundService(
                                    context, Intent(context, FallSensorService::class.java)
                                )
                                enabled = true
                            } else {
                                HapticFeedback.heavyClick(context)
                                context.stopService(Intent(context, FallSensorService::class.java))
                                enabled = false
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = SuccessGreen,
                            uncheckedTrackColor = WarningAmber
                        )
                    )
                }
            }

            if (!sensorAvailable) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = "This device does not expose an accelerometer, so automatic fall detection cannot run here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "How it works",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Impact detected.\n" +
                            "2. 10-second cancel window.\n" +
                            "3. SOS sent to your emergency contact.",
                        style = MaterialTheme.typography.labelLarge,
                        color = ErrorRed,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "The phone's accelerometer watches for a hard impact followed by stillness " +
                            "(the classic fall signature). When one is detected you get a loud 10-second " +
                            "countdown notification. Tap \"It's OK\" if you are fine, otherwise the app sends " +
                            "an SOS text with your live location to your emergency contact and mirrors it " +
                            "to Firebase so a guardian sees it.\n\n" +
                            "This runs as a background service and stays on until you switch it off.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun hasAccelerometer(context: Context): Boolean {
    val sm = context.getSystemService(Context.SENSOR_SERVICE) as? android.hardware.SensorManager
        ?: return false
    return sm.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER) != null
}

private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return false
    return am.getRunningServices(Int.MAX_VALUE).any { it.service.className == serviceClass.name }
}