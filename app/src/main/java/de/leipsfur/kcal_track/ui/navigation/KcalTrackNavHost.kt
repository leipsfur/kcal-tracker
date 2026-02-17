package de.leipsfur.kcal_track.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.leipsfur.kcal_track.KcalTrackApplication
import de.leipsfur.kcal_track.ui.activity.ActivityScreen
import de.leipsfur.kcal_track.ui.activity.ActivityViewModel
import de.leipsfur.kcal_track.ui.dashboard.DashboardScreen
import de.leipsfur.kcal_track.ui.food.FoodScreen
import de.leipsfur.kcal_track.ui.food.FoodViewModel
import de.leipsfur.kcal_track.ui.settings.SettingsScreen
import de.leipsfur.kcal_track.ui.settings.SettingsViewModel
import de.leipsfur.kcal_track.ui.weight.WeightScreen

@Composable
fun KcalTrackNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val application = LocalContext.current.applicationContext as KcalTrackApplication

    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Dashboard.route,
        modifier = modifier
    ) {
        composable(NavigationRoute.Dashboard.route) { DashboardScreen() }
        composable(NavigationRoute.Food.route) {
            val foodViewModel: FoodViewModel = viewModel(
                factory = FoodViewModel.Factory(application.foodRepository)
            )
            FoodScreen(viewModel = foodViewModel)
        }
        composable(NavigationRoute.Activity.route) {
            val activityViewModel: ActivityViewModel = viewModel(
                factory = ActivityViewModel.Factory(application.activityRepository)
            )
            ActivityScreen(viewModel = activityViewModel)
        }
        composable(NavigationRoute.Weight.route) { WeightScreen() }
        composable(NavigationRoute.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory(application.settingsRepository)
            )
            SettingsScreen(viewModel = settingsViewModel)
        }
    }
}
