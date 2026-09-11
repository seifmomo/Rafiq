package com.example.rafiq.presentation.payment

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rafiq.ui.components.RafiqTopBar
import com.example.rafiq.ui.theme.OnSurfaceVariant
import com.example.rafiq.ui.theme.SuccessGreen
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val activity = remember { context as? Activity }
    val walletOptions = remember {
        com.google.android.gms.wallet.Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
            .build()
    }
    val paymentsClient: PaymentsClient? = remember(activity, walletOptions) {
        activity?.let { Wallet.getPaymentsClient(it, walletOptions) }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        viewModel.onResult(result.resultCode, result.data)
    }

    LaunchedEffect(state) {
        val resolution = state as? PaymentState.NeedsResolution ?: return@LaunchedEffect
        try {
            resolution.status.startResolutionForResult(launcher)
        } catch (_: Exception) {
            // cancelled / unavailable — state is reset below
        }
        viewModel.reset()
    }

    Scaffold(
        topBar = {
            RafiqTopBar(
                title = "Payments",
                subtitle = "Pay your assistant securely",
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InvoiceCard()

            when (val current = state) {
                is PaymentState.Success -> {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.12f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.size(12.dp))
                                Column {
                                    Text("Payment approved (sandbox)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                    if (current.token != null) {
                                        Text(
                                            text = "Token: ${current.token.take(24)}…",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnSurfaceVariant
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    viewModel.reset()
                                    navController.popBackStack()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.White)
                            ) {
                                Text("Done", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                is PaymentState.Error -> {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = current.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is PaymentState.Cancelled -> {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "Payment cancelled.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {}
            }

            Button(
                onClick = {
                    if (paymentsClient != null) {
                        viewModel.pay(paymentsClient)
                    } else {
                        viewModel.onUnavailable()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                enabled = state !is PaymentState.Processing
            ) {
                if (state is PaymentState.Processing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Contacting Google Pay…", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Pay ${viewModel.amountLabel} with Google Pay", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(modifier = Modifier.padding(14.dp)) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "This is a Google Pay sandbox — no real charge.\n\n" +
                            "• Must be tested on a real phone with Google Play services (not an emulator).\n" +
                            "• Tokenization is wired to Stripe test parameters in PaymentViewModel; supply a real " +
                            "test merchant + publishable key to receive a usable payment token.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun InvoiceCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Booking Invoice",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Wheelchair assistance (2 h)", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "USD 12.50", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Service fee", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "USD 0.00", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(text = "Total due", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "USD 12.50", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = SuccessGreen)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Google Pay sandbox — no real charge",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}