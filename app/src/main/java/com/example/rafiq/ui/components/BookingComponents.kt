package com.example.rafiq.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rafiq.domain.model.Assistant
import com.example.rafiq.ui.theme.OnSurface
import com.example.rafiq.ui.theme.OnSurfaceVariant
import com.example.rafiq.ui.theme.SuccessGreen
import com.example.rafiq.ui.theme.SurfaceContainer
import com.example.rafiq.ui.theme.SurfaceContainerHigh
import com.example.rafiq.ui.theme.Teal
import com.example.rafiq.ui.theme.VividBlue
import com.example.rafiq.ui.theme.WarningAmber

/** Avatar circle showing a person's initials on a soft gradient background. */
@Composable
fun InitialsAvatar(name: String, size: Int = 46, modifier: Modifier = Modifier) {
    val initials = name
        .split(' ')
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull() }
        .joinToString("")
        .uppercase()

    Box(
        modifier = modifier
            .size(size.dp)
            .background(
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(Teal, VividBlue)
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size / 3 + 2).sp
        )
    }
}

/** Compact read-only rating row: star + numeric rating + review count. */
@Composable
fun RatingRow(rating: Double, reviews: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Default.Star,
            contentDescription = null,
            tint = WarningAmber,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = "%.1f".format(rating),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "($reviews reviews)",
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant
        )
    }
}

/** Wraps the given content with a title label used by booking form sections. */
@Composable
fun BookingSectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = OnSurfaceVariant
    )
}

/** A single selectable assistant, shown as a compact premium card. */
@Composable
fun AssistantCard(
    assistant: Assistant,
    selected: Boolean = false,
    showAction: Boolean = true,
    onSelect: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) SurfaceContainer else SurfaceCardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InitialsAvatar(name = assistant.name, size = 48)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = assistant.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    if (assistant.verified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        VerifiedBadge()
                    }
                }
                Text(
                    text = assistant.specialization,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                RatingRow(rating = assistant.rating, reviews = assistant.reviews)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "%.1f km".format(assistant.distanceKm),
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "%.0f/hr".format(assistant.hourlyPrice),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceVariant
                    )
                }
            }
            if (showAction) {
                Spacer(modifier = Modifier.width(10.dp))
                if (selected) {
                    Box(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Selected",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SuccessGreen
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onSelect,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VividBlue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VividBlue)
                    ) {
                        Text("Select", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun VerifiedBadge() {
    Box(
        modifier = Modifier
            .background(color = SuccessGreen.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "Verified",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = SuccessGreen
        )
    }
}

/** Horizontal row of small skill tags. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillTags(skills: List<String>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        maxItemsInEachRow = 3
    ) {
        skills.take(3).forEach { skill ->
            Box(
                modifier = Modifier
                    .background(color = SurfaceContainerHigh, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = skill,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

private val SurfaceCardColor = Color(0xFFF6FAFF)