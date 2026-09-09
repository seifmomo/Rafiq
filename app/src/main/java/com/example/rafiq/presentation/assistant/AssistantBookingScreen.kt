package com.example.rafiq.presentation.assistant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Elderly
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignLanguage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.rafiq.domain.model.Assistant
import com.example.rafiq.domain.model.Booking
import com.example.rafiq.domain.model.BookingRequest
import com.example.rafiq.domain.model.DisabilityNeed
import com.example.rafiq.ui.components.AssistantCard
import com.example.rafiq.ui.components.BookingSectionHeader
import com.example.rafiq.ui.components.RafiqEmptyState
import com.example.rafiq.ui.components.RafiqTopBar
import com.example.rafiq.ui.theme.OnSurface
import com.example.rafiq.ui.theme.OnSurfaceVariant
import com.example.rafiq.ui.theme.Outline
import com.example.rafiq.ui.theme.OutlineVariant
import com.example.rafiq.ui.theme.SuccessGreen
import com.example.rafiq.ui.theme.SurfaceContainer
import com.example.rafiq.ui.theme.SurfaceDim
import com.example.rafiq.ui.theme.Teal
import com.example.rafiq.ui.theme.VividBlue
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AssistantBookingScreen(
    navController: NavHostController,
    viewModel: AssistantBookingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(SurfaceDim)) {
        val title = when (state.step) {
            AssistantBookingSteps.RESULTS -> "Available Assistants"
            AssistantBookingSteps.SUMMARY -> "Review & Confirm"
            AssistantBookingSteps.CONFIRMED -> "Booking Confirmed"
            else -> "Assistant Booking"
        }
        val subtitle = if (state.step == AssistantBookingSteps.FORM) {
            "Find your perfect assistant"
        } else {
            null
        }
        RafiqTopBar(
            title = title,
            subtitle = subtitle,
            onBack = {
                if (state.step > AssistantBookingSteps.FORM) {
                    viewModel.goBack()
                } else {
                    navController.popBackStack()
                }
            }
        )

        if (state.step == AssistantBookingSteps.CONFIRMED) {
            ConfirmedStep(
                booking = state.booking,
                onDone = { navController.popBackStack() },
                onBookAnother = viewModel::resetAll
            )
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                BookingStepIndicator(step = state.step)
                Spacer(modifier = Modifier.height(6.dp))

                when (state.step) {
                    AssistantBookingSteps.RESULTS -> {
                        ResultsStep(
                            assistants = state.assistants,
                            searching = state.searching,
                            onSelect = viewModel::selectAssistant
                        )
                    }

                    AssistantBookingSteps.SUMMARY -> {
                        SummaryStep(
                            request = state.request,
                            assistant = state.selectedAssistant,
                            onSelectDifferent = viewModel::goBack,
                            onConfirm = viewModel::confirmBooking
                        )
                    }

                    else -> {
                        FormStep(
                            state = state,
                            onToggleNeed = viewModel::toggleNeed,
                            onFromChange = viewModel::setFromLocation,
                            onToChange = viewModel::setToLocation,
                            onDateChange = viewModel::setDate,
                            onTimeChange = viewModel::setTime,
                            onBudgetChange = viewModel::setBudgetPerHour,
                            onSearch = viewModel::findAssistants
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingStepIndicator(step: Int) {
    val steps = listOf("Needs", "Results", "Confirm")
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, label ->
            val active = index == step
            val done = index < step
            val color = when {
                done -> SuccessGreen
                active -> VividBlue
                else -> Outline
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(color.copy(alpha = 0.35f), RoundedCornerShape(2.dp))
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(color, RoundedCornerShape(2.dp))
            )
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(if (active) 8.dp else 6.dp)
                        .background(color, RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                    color = color
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FormStep(
    state: AssistantBookingUiState,
    onToggleNeed: (DisabilityNeed) -> Unit,
    onFromChange: (String) -> Unit,
    onToChange: (String) -> Unit,
    onDateChange: (Long?) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onBudgetChange: (Float) -> Unit,
    onSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        BookingSectionHeader("What assistance do you need?")
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DisabilityNeed.entries.forEach { need ->
                val selected = need in state.selectedNeeds
                FilterChip(
                    selected = selected,
                    onClick = { onToggleNeed(need) },
                    label = { Text(need.label, style = MaterialTheme.typography.labelMedium) },
                    leadingIcon = {
                        Icon(
                            imageVector = needIcon(need),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SurfaceContainer,
                        selectedContainerColor = VividBlue.copy(alpha = 0.12f),
                        labelColor = OnSurfaceVariant,
                        selectedLabelColor = VividBlue
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        BookingSectionHeader("Your journey")
        Spacer(modifier = Modifier.height(8.dp))

        BookingTextField(
            value = state.fromLocation,
            onValueChange = onFromChange,
            label = "Booking from",
            placeholder = "e.g. Nasr City, Cairo",
            icon = Icons.Default.Place
        )
        Spacer(modifier = Modifier.height(10.dp))
        BookingTextField(
            value = state.toLocation,
            onValueChange = onToChange,
            label = "Going to",
            placeholder = "e.g. Tahrir Square",
            icon = Icons.Default.Flag
        )

        Spacer(modifier = Modifier.height(20.dp))
        BookingSectionHeader("When?")
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DateField(
                value = state.preferredDate,
                onPick = onDateChange,
                modifier = Modifier.weight(1f)
            )
            TimeField(
                value = state.preferredTime,
                onPick = onTimeChange,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        BookingSectionHeader("Hourly budget")
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${state.budgetPerHour.toInt()} EGP/hr",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = VividBlue
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "max",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
            )
        }
        Slider(
            value = state.budgetPerHour,
            onValueChange = onBudgetChange,
            valueRange = 50f..500f,
            steps = 17,
            colors = SliderDefaults.colors(
                thumbColor = VividBlue,
                activeTrackColor = VividBlue
            )
        )

        Spacer(modifier = Modifier.height(24.dp))
        val canSearch = state.selectedNeeds.isNotEmpty() &&
            state.fromLocation.isNotBlank() &&
            state.toLocation.isNotBlank()
        PrimaryBookingButton(
            text = "Find Assistants",
            icon = Icons.Filled.Search,
            enabled = canSearch && !state.searching,
            loading = state.searching,
            onClick = onSearch
        )
        if (!canSearch && state.selectedNeeds.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add your start and destination to continue.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder, color = OnSurfaceVariant.copy(alpha = 0.7f)) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = VividBlue) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    value: String,
    onPick: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }
    val parsedEpoch = remember(value) {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(value)?.time
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = parsedEpoch)

    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = modifier.clickable { showPicker = true },
        readOnly = true,
        label = { Text("Date") },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onPick(datePickerState.selectedDateMillis)
                    showPicker = false
                }) {
                    Text("OK", color = VividBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel", color = OnSurfaceVariant)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeField(
    value: String,
    onPick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }
    val parts = remember(value) {
        val split = value.split(":")
        (split.getOrNull(0)?.toIntOrNull() ?: 9) to (split.getOrNull(1)?.toIntOrNull() ?: 0)
    }
    val timePickerState = rememberTimePickerState(
        initialHour = parts.first,
        initialMinute = parts.second,
        is24Hour = true
    )

    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = modifier.clickable { showPicker = true },
        readOnly = true,
        label = { Text("Time") },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onPick(timePickerState.hour, timePickerState.minute)
                    showPicker = false
                }) {
                    Text("OK", color = VividBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel", color = OnSurfaceVariant)
                }
            },
            title = { Text("Select time") },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
private fun ResultsStep(
    assistants: List<Assistant>,
    searching: Boolean,
    onSelect: (Assistant) -> Unit
) {
    if (searching) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = VividBlue)
        }
        return
    }

    if (assistants.isEmpty()) {
        RafiqEmptyState(
            icon = Icons.Default.Search,
            title = "No assistants found",
            subtitle = "Try widening your budget or adding different needs.",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "${assistants.size} assistant${if (assistants.size == 1) "" else "s"} available",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(assistants, key = { it.id }) { assistant ->
                AssistantCard(
                    assistant = assistant,
                    onSelect = { onSelect(assistant) }
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun SummaryStep(
    request: BookingRequest,
    assistant: Assistant?,
    onSelectDifferent: () -> Unit,
    onConfirm: () -> Unit
) {
    if (assistant == null) {
        RafiqEmptyState(
            icon = Icons.Default.Search,
            title = "Nothing selected yet",
            subtitle = "Go back and pick an assistant to continue.",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    val hours = assistantDistanceEstimate(assistant.distanceKm)
    val cost = (hours * assistant.hourlyPrice).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TripSummaryCard(request = request)
        Spacer(modifier = Modifier.height(12.dp))

        BookingSectionHeader("Your assistant")
        Spacer(modifier = Modifier.height(8.dp))
        AssistantCard(assistant = assistant, showAction = false)

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Estimated assistance",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${"%.1f".format(hours)} hr",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Estimated total",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$cost EGP",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = VividBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onSelectDifferent,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceContainer,
                    contentColor = OnSurface
                )
            ) {
                Text("Change")
            }
            PrimaryBookingButton(
                text = "Confirm Booking",
                icon = Icons.Default.CheckCircle,
                enabled = true,
                loading = false,
                onClick = onConfirm,
                modifier = Modifier.weight(1.5f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TripSummaryCard(request: BookingRequest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    tint = Teal,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = request.fromLocation,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = OnSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Outline,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = request.toLocation,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = OnSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = OutlineVariant)
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${request.preferredDate} at ${request.preferredTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${request.budgetPerHour.toInt()} EGP/hr max",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ConfirmedStep(
    booking: Booking?,
    onDone: () -> Unit,
    onBookAnother: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    brush = Brush.linearGradient(listOf(SuccessGreen, Teal)),
                    shape = RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "You're all set!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        if (booking != null) {
            Text(
                text = "${booking.assistant.name} will meet you on ${booking.request.preferredDate} at ${booking.request.preferredTime}.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = OnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DetailRow("Booking reference", booking.id)
                    DetailRow("Assistant", booking.assistant.name)
                    DetailRow("Estimated hours", "${"%.1f".format(booking.estimatedHours)} hr")
                    DetailRow("Estimated cost", "${booking.estimatedCost.toInt()} EGP")
                }
            }
        }
        Spacer(modifier = Modifier.weight(1.5f))
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VividBlue)
        ) {
            Text("Done", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextButton(onClick = onBookAnother) {
            Icon(
                Icons.Default.RestartAlt,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Book another assistant")
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )
    }
}

@Composable
private fun OutlinedTextButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Outline),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            content()
        }
    }
}

@Composable
private fun PrimaryBookingButton(
    text: String,
    icon: ImageVector,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(listOf(VividBlue, Teal)),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun needIcon(need: DisabilityNeed): ImageVector = when (need) {
    DisabilityNeed.WHEELCHAIR -> Icons.Default.Accessible
    DisabilityNeed.HEARING -> Icons.Default.Hearing
    DisabilityNeed.SIGN_LANGUAGE -> Icons.Default.SignLanguage
    DisabilityNeed.VISUAL -> Icons.Default.Visibility
    DisabilityNeed.ELDERLY -> Icons.Default.Elderly
    DisabilityNeed.MULTIPLE -> Icons.Default.Groups
}

/** Extension to build a BookingRequest from the current UI state. */
private val AssistantBookingUiState.request: BookingRequest
    get() = BookingRequest(
        needs = selectedNeeds,
        fromLocation = fromLocation.trim(),
        toLocation = toLocation.trim(),
        preferredDate = preferredDate,
        preferredTime = preferredTime,
        budgetPerHour = budgetPerHour.toDouble()
    )

/** Rough estimate: 15 km covered per hour of assistance, minimum 1 hour. */
internal fun assistantDistanceEstimate(distanceKm: Double): Double {
    val tripHours = distanceKm / 15.0
    val rounded = (tripHours.coerceAtLeast(1.0) * 2).toInt() / 2.0
    return rounded
}