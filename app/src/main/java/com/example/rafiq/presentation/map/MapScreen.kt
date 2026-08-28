package com.example.rafiq.presentation.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.WheelchairPickup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.rafiq.data.local.EquippedPlaceEntity
import com.example.rafiq.presentation.navigation.Screen
import com.example.rafiq.ui.components.RafiqTopBar
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private val CAIRO = GeoPoint(30.0444, 31.2357)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val dbPlaces by viewModel.places.collectAsState()

    Scaffold(
        topBar = {
            RafiqTopBar(
                title = "Map & Places",
                subtitle = "Explore equipped locations",
                onBack = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddPlace.route) },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Place to earn points")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LiveMapView(places = dbPlaces)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nearby Equipped Places",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .semantics { contentDescription = "List of nearby equipped places" }
            )

            if (dbPlaces.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No places added yet", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Tap + to add accessible places and earn points!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(dbPlaces, key = { it.id }) { place ->
                        val features = mutableListOf<String>()
                        if (place.isWheelchairAccessible) features.add("Wheelchair accessible")
                        if (place.hasSignLanguageSupport) features.add("Sign language support")
                        if (place.hasBrailleSignage) features.add("Braille signage")

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentDescription = "Place: ${place.name}, Features: ${features.joinToString(", ")}" },
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (place.isWheelchairAccessible) Icons.Default.WheelchairPickup else Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = place.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(text = features.joinToString(" · "), style = MaterialTheme.typography.bodyMedium)
                                    if (place.description.isNotBlank()) {
                                        Text(text = place.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveMapView(places: List<EquippedPlaceEntity>) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView?.onDetach()
            mapView = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .semantics { contentDescription = "Real map of Cairo showing nearby equipped places" }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(14.0)
                    controller.setCenter(CAIRO)
                    mapView = this
                }
            },
            update = { view ->
                view.overlays.clear()

                if (places.isEmpty()) {
                    Marker(view).apply {
                        position = CAIRO
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Cairo"
                        snippet = "You are viewing Cairo"
                        view.overlays.add(this)
                    }
                } else {
                    places.forEach { place ->
                        Marker(view).apply {
                            position = GeoPoint(place.latitude, place.longitude)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = place.name
                            val features = buildList {
                                if (place.isWheelchairAccessible) add("Wheelchair accessible")
                                if (place.hasSignLanguageSupport) add("Sign language support")
                                if (place.hasBrailleSignage) add("Braille signage")
                            }
                            snippet = features.joinToString(", ").ifBlank { place.description.ifBlank { "Equipped place" } }
                            view.overlays.add(this)
                        }
                    }
                    view.controller.animateTo(GeoPoint(places.first().latitude, places.first().longitude), 13.0, 800L)
                }
                view.invalidate()
            }
        )

        // Cairo tag overlay
        Text(
            text = "Cairo · OpenStreetMap",
            color = Color.White,
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .background(Color(0x990F172A), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
