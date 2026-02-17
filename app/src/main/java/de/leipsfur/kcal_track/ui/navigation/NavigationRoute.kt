package de.leipsfur.kcal_track.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import de.leipsfur.kcal_track.R

enum class NavigationRoute(
    val route: String,
    val labelResId: Int,
    val icon: ImageVector
) {
    Dashboard("dashboard", R.string.nav_dashboard, Icons.Filled.Home),
    Food("food", R.string.nav_food, Icons.Filled.Restaurant),
    Activity("activity", R.string.nav_activity, Icons.Filled.DirectionsRun),
    Weight("weight", R.string.nav_weight, Icons.Filled.MonitorWeight),
    Settings("settings", R.string.nav_settings, Icons.Filled.Settings)
}
