package com.example.rafiq.presentation.tracking

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.rafiq.ui.components.RafiqTopBar
import com.example.rafiq.ui.theme.Cyan
import com.example.rafiq.ui.theme.ErrorRed
import com.example.rafiq.ui.theme.OnSurfaceVariant
import com.example.rafiq.ui.theme.SuccessGreen
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private val CAIRO = LatLng(30.0444, 31.2357)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTrackingScreen(
    navController: NavController,
    viewModel: LiveTrackingViewModel = hiltViewModel()
) {
    val marker by viewModel.marker.collectAsState()
    val sharing by viewModel.sharing.collectAsState()
    val trackingEnabled by viewModel.trackingEnabled.collectAsState()
    val firebaseConfigured by viewModel.firebaseConfigured.collectAsState()
    val lastUpdateText by viewModel.lastUpdateText.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            RafiqTopBar(
                title = "Live Tracking",
                subtitle = "Share & follow live location",
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                TrackingMapView(marker = marker)
            }

            if (!firebaseConfigured) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Firebase isn't configured on this build yet, so live tiles won't update. " +
                                "Add google-services.json to enable cloud syncing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Cyan
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lastUpdateText,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (trackingEnabled && marker != null)
                                    "Live position ${"%.5f".format(marker!!.first)}, ${"%.5f".format(marker!!.second)}"
                                else if (trackingEnabled && marker == null)
                                    "Enabled — waiting for the first ping…"
                                else
                                    "Enable tracking to watch the live marker move",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                        IconButton(onClick = {
                            marker?.let {
                                val uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${it.first},${it.second}")
                                try {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                } catch (_: ActivityNotFoundException) {
                                }
                            }
                        }, enabled = marker != null) {
                            Icon(
                                Icons.Default.Navigation,
                                contentDescription = "Open in Google Maps",
                                tint = Cyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!sharing) {
                        Button(
                            onClick = { viewModel.startSharing() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SuccessGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start sharing my location", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { viewModel.enableTracking() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(top = 4.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Track (read mode — show where my location pings land)")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.stopSharing() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ErrorRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Stop sharing my location", fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "Your location is being published to Firebase every ~4s. Anyone with your user id can follow you on the map above.",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun TrackingMapView(marker: Pair<Double, Double>?) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView?.onCreate(null)
                Lifecycle.Event.ON_START -> mapView?.onStart()
                Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                Lifecycle.Event.ON_STOP -> mapView?.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView?.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView?.onDestroy()
            mapView = null
            googleMap = null
        }
    }

    LaunchedEffect(mapView) {
        val view = mapView ?: return@LaunchedEffect
        view.getMapAsync { gmap ->
            googleMap = gmap
            gmap.uiSettings.isZoomControlsEnabled = true
            gmap.uiSettings.isCompassEnabled = true
            gmap.uiSettings.isMyLocationButtonEnabled = true
            gmap.uiSettings.setAllGesturesEnabled(true)
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(CAIRO, 13f))
        }
    }

    LaunchedEffect(googleMap, marker) {
        val gmap = googleMap ?: return@LaunchedEffect
        val position = marker ?: return@LaunchedEffect
        gmap.clear()
        gmap.addMarker(
            MarkerOptions()
                .position(LatLng(position.first, position.second))
                .title("Live location")
        )
        gmap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(position.first, position.second), 16f)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    onCreate(null)
                    mapView = this
                }
            }
        )

        Text(
            text = "Map data © Google",
            color = Color.White,
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .background(Color(0x990F172A), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}