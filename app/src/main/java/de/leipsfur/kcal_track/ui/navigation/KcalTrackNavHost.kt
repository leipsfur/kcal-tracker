package de.leipsfur.kcal_track.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.leipsfur.kcal_track.ui.activity.ActivityScreen
import de.leipsfur.kcal_track.ui.dashboard.DashboardScreen
import de.leipsfur.kcal_track.ui.food.FoodScreen
import de.leipsfur.kcal_track.ui.settings.SettingsScreen
import de.leipsfur.kcal_track.ui.weight.WeightScreen

@Composable
fun KcalTrackNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Dashboard.route,
        modifier = modifier
    ) {
        composable(NavigationRoute.Dashboard.route) { DashboardScreen() }
        composable(NavigationRoute.Food.route) { FoodScreen() }
        composable(NavigationRoute.Activity.route) { ActivityScreen() }
        composable(NavigationRoute.Weight.route) { WeightScreen() }
        composable(NavigationRoute.Settings.route) { SettingsScreen() }
    }
}
