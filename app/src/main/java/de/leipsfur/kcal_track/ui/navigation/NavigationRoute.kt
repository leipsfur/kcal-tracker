package de.leipsfur.kcal_track.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import de.leipsfur.kcal_track.R

enum class NavigationRoute(
    val route: String,
    val labelResId: Int,
    val icon: ImageVector
) {
    Dashboard("dashboard", R.string.nav_dashboard, Icons.Outlined.Dashboard),
    Food("food", R.string.nav_food, Icons.Outlined.Fastfood),
    Activity("activity", R.string.nav_activity, Icons.Outlined.FitnessCenter),
    Weight("weight", R.string.nav_weight, Icons.Outlined.Scale),
    Settings("settings", R.string.nav_settings, Icons.Outlined.Settings)
}
