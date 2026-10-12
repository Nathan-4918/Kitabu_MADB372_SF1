package com.nathan.kitabu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nathan.kitabu.data.local.BookingWithBook
import com.nathan.kitabu.ui.components.BookingCard
import com.nathan.kitabu.ui.viewmodel.LibraryViewModel

@Composable
fun BookingsScreen(
    viewModel: LibraryViewModel,
    modifier: Modifier = Modifier
) {
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()

    BookingsContent(
        bookings = bookings,
        onCollect = { viewModel.collect(it) },
        onRenew = { viewModel.renew(it) },
        onReturn = { viewModel.returnBook(it) },
        onCancel = { viewModel.cancel(it) },
        modifier = modifier
    )
}

@Composable
fun BookingsContent(
    bookings: List<BookingWithBook>,
    onCollect: (BookingWithBook) -> Unit,
    onRenew: (BookingWithBook) -> Unit,
    onReturn: (BookingWithBook) -> Unit,
    onCancel: (BookingWithBook) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(
            text = "My Reservations",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp)
        )

        if (bookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No active reservations")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookings, key = { it.bookingId }) { booking ->
                    BookingCard(
                        booking = booking,
                        onCollect = { onCollect(booking) },
                        onRenew = { onRenew(booking) },
                        onReturn = { onReturn(booking) },
                        onCancel = { onCancel(booking) }
                    )
                }
            }
        }
    }
}