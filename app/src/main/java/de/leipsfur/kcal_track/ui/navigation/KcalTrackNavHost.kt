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
import de.leipsfur.kcal_track.ui.dashboard.DashboardViewModel
import de.leipsfur.kcal_track.ui.food.FoodScreen
import de.leipsfur.kcal_track.ui.food.FoodViewModel
import de.leipsfur.kcal_track.ui.settings.SettingsScreen
import de.leipsfur.kcal_track.ui.settings.SettingsViewModel
import de.leipsfur.kcal_track.ui.weight.WeightScreen

import de.leipsfur.kcal_track.ui.shared.DateViewModel
import de.leipsfur.kcal_track.ui.weight.WeightViewModel // Import if needed or remove if unused yet

@Composable
fun KcalTrackNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val application = LocalContext.current.applicationContext as KcalTrackApplication
    
    // Shared DateViewModel scoped to the activity/navhost parent
    val dateViewModel: DateViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Dashboard.route,
        modifier = modifier
    ) {
        composable(NavigationRoute.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModel.Factory(
                    application.foodRepository,
                    application.activityRepository,
                    application.settingsRepository,
                    dateViewModel.selectedDate,
                    dateViewModel::onDateChanged
                )
            )
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToSettings = {
                    navController.navigate(NavigationRoute.Settings.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                }
            )
        }
        composable(NavigationRoute.Food.route) {
            val foodViewModel: FoodViewModel = viewModel(
                factory = FoodViewModel.Factory(
                    application.foodRepository,
                    dateViewModel.selectedDate,
                    dateViewModel::onDateChanged
                )
            )
            FoodScreen(viewModel = foodViewModel)
        }
        composable(NavigationRoute.Activity.route) {
            val activityViewModel: ActivityViewModel = viewModel(
                factory = ActivityViewModel.Factory(
                    application.activityRepository,
                    dateViewModel.selectedDate,
                    dateViewModel::onDateChanged
                )
            )
            ActivityScreen(viewModel = activityViewModel)
        }
        composable(NavigationRoute.Weight.route) {
            val weightViewModel: WeightViewModel = viewModel(
                factory = WeightViewModel.Factory(application.weightRepository)
            )
            WeightScreen(viewModel = weightViewModel)
        }
        composable(NavigationRoute.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory(application.settingsRepository)
            )
            SettingsScreen(viewModel = settingsViewModel)
        }
    }
}
