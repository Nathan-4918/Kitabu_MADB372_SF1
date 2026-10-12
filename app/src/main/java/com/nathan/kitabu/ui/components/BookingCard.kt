package com.nathan.kitabu.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nathan.kitabu.data.local.BookingStatus
import com.nathan.kitabu.data.local.BookingWithBook
import com.nathan.kitabu.domain.daysRemaining
import com.nathan.kitabu.domain.formatDate

@Composable
fun BookingCard(
    booking: BookingWithBook,
    onCollect: () -> Unit,
    onRenew: () -> Unit,
    onReturn: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val days = daysRemaining(booking.returnDeadline)
    val daysText = when {
        days < 0 -> "Overdue by ${-days} day(s)"
        days == 0 -> "Due today"
        days == 1 -> "1 day left"
        else -> "$days days left"
    }
    val daysColor = if (days < 0) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.onSurfaceVariant
    val isPending = booking.status == BookingStatus.PENDING
    val isActive = booking.status == BookingStatus.ACTIVE

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = booking.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (isPending) "Pending pickup" else "Active",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(booking.author, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Reserved: ${formatDate(booking.bookingDate)}")
            Text("Return by: ${formatDate(booking.returnDeadline)}")
            Text(daysText, color = daysColor, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            if (isPending) {
                Button(onClick = onCollect, modifier = Modifier.fillMaxWidth()) {
                    Text("Mark as collected")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onRenew,
                    enabled = isActive,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.weight(1f)
                ) { Text("Renew") }
                OutlinedButton(
                    onClick = onReturn,
                    enabled = isActive,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.weight(1f)
                ) { Text("Return") }
                OutlinedButton(
                    onClick = onCancel,
                    enabled = isPending,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.weight(1f)
                ) { Text("Cancel") }
            }
        }
    }
}