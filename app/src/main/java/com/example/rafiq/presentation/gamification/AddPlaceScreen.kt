package com.example.rafiq.presentation.gamification

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceScreen(
    navController: NavController,
    viewModel: AddPlaceViewModel = hiltViewModel()
) {
    var placeName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isWheelchairAccessible by remember { mutableStateOf(false) }
    var hasSignLanguage by remember { mutableStateOf(false) }
    var hasBrailleSignage by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val saveState by viewModel.saveState.collectAsState()
    val context = LocalContext.current

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    // Location permission
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    // Request location permission on first launch
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // React to save state changes
    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is AddPlaceViewModel.SaveState.Success -> {
                val locationNote = if (state.locationCaptured) "" else " (Location not available)"
                snackbarHostState.showSnackbar(
                    "Place submitted successfully! +50 Points$locationNote"
                )
                delay(800)
                viewModel.resetSaveState()
                navController.popBackStack()
            }

            is AddPlaceViewModel.SaveState.Error -> {
                snackbarHostState.showSnackbar("Error: ${state.message}")
                viewModel.resetSaveState()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Equipped Place") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(tween(500)) { it / 2 } + fadeIn(tween(500))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Gamification Banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Earn 50 Points for adding a verified place!",
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        "Place Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = placeName,
                        onValueChange = { placeName = it },
                        label = { Text("Name of the Place") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Enter the name of the place"
                            },
                        singleLine = true,
                        isError = placeName.isBlank() && placeName.isNotEmpty()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description or specific instructions") },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Enter description"
                            },
                        minLines = 3
                    )

                    Text(
                        "Accessibility Features",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isWheelchairAccessible,
                            onCheckedChange = { isWheelchairAccessible = it }
                        )
                        Text("Wheelchair Accessible")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = hasSignLanguage,
                            onCheckedChange = { hasSignLanguage = it }
                        )
                        Text("Sign Language Support Available")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = hasBrailleSignage,
                            onCheckedChange = { hasBrailleSignage = it }
                        )
                        Text("Braille Signage Available")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val isSaving = saveState is AddPlaceViewModel.SaveState.Saving
                    Button(
                        onClick = {
                            if (placeName.isBlank()) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Please enter a place name.")
                                }
                            } else if (!isWheelchairAccessible && !hasSignLanguage && !hasBrailleSignage) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Please select at least one accessibility feature.")
                                }
                            } else {
                                viewModel.savePlace(
                                    name = placeName,
                                    description = description,
                                    isWheelchairAccessible = isWheelchairAccessible,
                                    hasSignLanguage = hasSignLanguage,
                                    hasBrailleSignage = hasBrailleSignage
                                )
                            }
                        },
                        enabled = !isSaving,
                        shape = RoundedCornerShape(24.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 2.dp
                        ),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics {
                                contentDescription = "Submit place and earn points"
                            }
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            if (isSaving) "Saving..." else "Submit Place & Earn Points",
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
