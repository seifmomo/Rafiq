package com.example.rafiq.presentation.bookinghistory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.rafiq.domain.model.BookingRecord
import com.example.rafiq.presentation.navigation.Screen
import com.example.rafiq.ui.components.RafiqEmptyState
import com.example.rafiq.ui.components.RafiqTopBar
import com.example.rafiq.ui.theme.OnSurface
import com.example.rafiq.ui.theme.OnSurfaceVariant
import com.example.rafiq.ui.theme.Outline
import com.example.rafiq.ui.theme.OutlineVariant
import com.example.rafiq.ui.theme.SuccessGreen
import com.example.rafiq.ui.theme.SurfaceDim
import com.example.rafiq.ui.theme.Teal
import com.example.rafiq.ui.theme.VividBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookingHistoryScreen(
    navController: NavHostController,
    viewModel: BookingHistoryViewModel = hiltViewModel()
) {
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(SurfaceDim)) {
        RafiqTopBar(
            title = "My Bookings",
            subtitle = "Your assistant history & invoices",
            onBack = { navController.popBackStack() }
        )

        if (bookings.isEmpty()) {
            RafiqEmptyState(
                icon = Icons.Default.ReceiptLong,
                title = "No bookings yet",
                subtitle = "Book an assistant and your history + invoice will appear here.",
                modifier = Modifier.fillMaxSize()
            )
            return@Column
        }

        val totalSpent = bookings.sumOf { it.estimatedCost }.toInt()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Teal.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total spent", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            "$totalSpent EGP",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = VividBlue
                        )
                    }
                }
            }

            items(bookings, key = { it.id }) { booking ->
                BookingHistoryCard(
                    booking = booking,
                    onRate = { viewModel.rateBooking(booking.id, it) },
                    onDelete = { viewModel.deleteBooking(booking.id) },
                    onViewInvoice = { navController.navigate(Screen.BookingInvoice.withId(booking.id)) }
                )
            }
        }
    }
}

@Composable
private fun BookingHistoryCard(
    booking: BookingRecord,
    onRate: (Int) -> Unit,
    onDelete: () -> Unit,
    onViewInvoice: () -> Unit
) {
    var showRating by remember { mutableStateOf(booking.rating == null) }
    var rating by remember { mutableStateOf(booking.rating ?: 0) }

    val dateLabel = remember(booking.confirmedAt) {
        SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(booking.confirmedAt))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.assistantName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Text(
                        text = "${booking.assistantSpecialization} · $dateLabel",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
                Text(
                    text = "${booking.estimatedCost.toInt()} EGP",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = VividBlue
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = Teal,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = booking.id,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                if (booking.isRecurring) {
                    Text(
                        text = "Weekly",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = booking.fromLocation,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurface
                )
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Outline,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = booking.toLocation,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurface,
                    maxLines = 1
                )
            }
            Text(
                text = "${booking.preferredDate} at ${booking.preferredTime} · ${"%.1f".format(booking.estimatedHours)} hr",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            if (showRating) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Rate your visit:",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    (1..5).forEach { star ->
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "$star stars",
                            tint = if (star <= rating) OutlineVariant else Outline,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable {
                                    rating = star
                                    showRating = false
                                    onRate(star)
                                }
                        )
                    }
                    if (rating > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "$rating/5",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = VividBlue
                        )
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Your rating:",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = OutlineVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$rating/5",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onViewInvoice) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = VividBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Invoice", color = VividBlue)
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete booking",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}