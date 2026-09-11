package com.example.rafiq.presentation.bookinghistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.rafiq.ui.components.InitialsAvatar
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

@Composable
fun BookingInvoiceScreen(
    bookingId: String,
    navController: NavHostController,
    viewModel: BookingHistoryViewModel = hiltViewModel()
) {
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val booking = bookings.firstOrNull { it.id == bookingId }

    Column(modifier = Modifier.fillMaxSize().background(SurfaceDim)) {
        RafiqTopBar(
            title = "Invoice",
            subtitle = "Booking details",
            onBack = { navController.popBackStack() }
        )

        if (booking == null) {
            RafiqEmptyState(
                icon = Icons.Default.ReceiptLong,
                title = "Booking not found",
                subtitle = "This booking may have been deleted.",
                modifier = Modifier.fillMaxSize()
            )
            return@Column
        }

        InvoiceContent(booking = booking)
    }
}

@Composable
private fun InvoiceContent(booking: com.example.rafiq.domain.model.BookingRecord) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Invoice",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Text(
                    booking.id,
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InitialsAvatar(name = booking.assistantName, size = 44)
                    Spacer(modifier = Modifier.padding(start = 10.dp))
                    Column {
                        Text(
                            booking.assistantName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                        Text(
                            booking.assistantSpecialization,
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        if (booking.assistantVerified) {
                            Text(
                                "Verified",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                InvoiceRow("Booking reference", booking.id)
                InvoiceRow("Date & time", "${booking.preferredDate} at ${booking.preferredTime}")
                InvoiceRow("Schedule", if (booking.isRecurring) "Repeats weekly" else "One-time")
                HorizontalDivider(color = OutlineVariant, modifier = Modifier.padding(vertical = 10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = null,
                        tint = Teal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.padding(start = 6.dp))
                    Text(
                        booking.fromLocation,
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
                    Spacer(modifier = Modifier.padding(start = 6.dp))
                    Text(
                        booking.toLocation,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = OnSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    "Cost breakdown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                InvoiceRow("Assistant rate", "${booking.assistantHourlyPrice.toInt()} EGP/hour")
                InvoiceRow("Estimated hours", "${"%.1f".format(booking.estimatedHours)} hr")
                HorizontalDivider(color = OutlineVariant, modifier = Modifier.padding(vertical = 10.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Total",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "${booking.estimatedCost.toInt()} EGP",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = VividBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "An official invoice & payment confirmation are shared with your assistant. Contact support for billing questions.",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (booking.rating != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text(
                        "You rated this booking ${booking.rating}/5",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = OnSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun InvoiceRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )
    }
}