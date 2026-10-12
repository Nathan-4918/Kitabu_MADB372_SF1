package com.nathan.kitabu.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Catalog : Screen("catalog", "Catalog", Icons.Default.Home)
    object Bookings : Screen("bookings", "Reservations", Icons.Default.DateRange)
}