package de.leipsfur.kcal_track

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import de.leipsfur.kcal_track.ui.UiTestTags
import de.leipsfur.kcal_track.ui.activity.ActivityScreen
import de.leipsfur.kcal_track.ui.activity.ActivityViewModel
import de.leipsfur.kcal_track.ui.dashboard.DashboardScreen
import de.leipsfur.kcal_track.ui.dashboard.DashboardViewModel
import de.leipsfur.kcal_track.ui.food.FoodScreen
import de.leipsfur.kcal_track.ui.food.FoodViewModel
import de.leipsfur.kcal_track.ui.navigation.NavigationRoute
import de.leipsfur.kcal_track.ui.settings.SettingsScreen
import de.leipsfur.kcal_track.ui.settings.SettingsViewModel
import de.leipsfur.kcal_track.ui.shared.DateViewModel
import de.leipsfur.kcal_track.ui.theme.KcaltrackTheme
import de.leipsfur.kcal_track.ui.recipe.RecipeScreen
import de.leipsfur.kcal_track.ui.recipe.RecipeViewModel
import de.leipsfur.kcal_track.ui.weight.WeightScreen
import de.leipsfur.kcal_track.ui.weight.WeightViewModel
import de.leipsfur.kcal_track.widget.KcalTrackWidgetManager
import kotlinx.coroutines.launch
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var quickAdd = intent?.getBooleanExtra("quick_add", false) ?: false
        if (quickAdd) {
            intent?.removeExtra("quick_add")
        }

        setContent {
            KcaltrackTheme {
                KcalTrackApp(openQuickAdd = quickAdd)
            }
        }
    }
}

@Composable
fun KcalTrackApp(openQuickAdd: Boolean = false) {
    val application = LocalContext.current.applicationContext as KcalTrackApplication
    val dateViewModel: DateViewModel = viewModel()
    val quickAddState = rememberSaveable { mutableStateOf(openQuickAdd) }
    val scope = rememberCoroutineScope()

    val orderedRoutes = NavigationRoute.entries
    val tabCount = orderedRoutes.size
    val dashboardIndex = tabIndexOrDefault(NavigationRoute.Dashboard, 0)
    val foodIndex = tabIndexOrDefault(NavigationRoute.Food, 1)
    val activityIndex = tabIndexOrDefault(NavigationRoute.Activity, 2)
    val weightIndex = tabIndexOrDefault(NavigationRoute.Weight, 3)
    val recipeIndex = tabIndexOrDefault(NavigationRoute.Recipe, 4)

    var showSettings by rememberSaveable { mutableStateOf(false) }

    val onDataChanged: () -> Unit = remember(application) {
        { KcalTrackWidgetManager.updateWidget(application) }
    }

    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.Factory(
            application.foodRepository,
            application.activityRepository,
            application.settingsRepository,
            dateViewModel.selectedDate,
            dateViewModel::onDateChanged
        )
    )
    val foodViewModel: FoodViewModel = viewModel(
        factory = FoodViewModel.Factory(
            application.foodRepository,
            dateViewModel.selectedDate,
            dateViewModel::onDateChanged,
            onDataChanged
        )
    )
    val activityViewModel: ActivityViewModel = viewModel(
        factory = ActivityViewModel.Factory(
            application.activityRepository,
            dateViewModel.selectedDate,
            dateViewModel::onDateChanged,
            onDataChanged
        )
    )
    val weightViewModel: WeightViewModel = viewModel(
        factory = WeightViewModel.Factory(application.weightRepository)
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(
            application.settingsRepository,
            application.backupManager,
            onDataChanged
        )
    )
    val recipeViewModel: RecipeViewModel = viewModel(
        factory = RecipeViewModel.Factory(
            application.recipeRepository,
            application.foodRepository,
            dateViewModel.selectedDate,
            onDataChanged
        )
    )

    val startRealPage = if (openQuickAdd) foodIndex else dashboardIndex
    val initialPagerPage = remember(startRealPage, tabCount) {
        calculateInitialPagerPage(tabCount = tabCount, startRealPage = startRealPage)
    }
    val pagerState = rememberPagerState(initialPage = initialPagerPage) { Int.MAX_VALUE }
    val currentRealPage = pagerState.currentPage.floorMod(tabCount)

    val animateToRealPage: (Int) -> Unit = { targetRealPage ->
        scope.launch {
            val targetPage = calculateNearestPagerPage(
                currentPage = pagerState.currentPage,
                currentRealPage = currentRealPage,
                targetRealPage = targetRealPage.floorMod(tabCount),
                tabCount = tabCount
            )
            pagerState.animateScrollToPage(targetPage)
        }
    }

    LaunchedEffect(quickAddState.value, currentRealPage) {
        if (quickAddState.value && currentRealPage == foodIndex) {
            foodViewModel.showAddFromTemplateSheet()
            quickAddState.value = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    NavigationRoute.entries.forEachIndexed { index, route ->
                        NavigationBarItem(
                            selected = currentRealPage == index,
                            onClick = { animateToRealPage(index) },
                            icon = {
                                Icon(
                                    imageVector = route.icon,
                                    contentDescription = stringResource(route.labelResId)
                                )
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .testTag(UiTestTags.MAIN_TAB_SWIPE_CONTAINER)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page.floorMod(tabCount)) {
                        dashboardIndex -> DashboardScreen(
                            viewModel = dashboardViewModel,
                            onNavigateToSettings = { showSettings = true },
                            modifier = Modifier.fillMaxSize()
                        )
                        foodIndex -> FoodScreen(
                            viewModel = foodViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                        activityIndex -> ActivityScreen(
                            viewModel = activityViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                        weightIndex -> WeightScreen(
                            viewModel = weightViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                        recipeIndex -> RecipeScreen(
                            viewModel = recipeViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                        else -> DashboardScreen(
                            viewModel = dashboardViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        if (showSettings) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { showSettings = false },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private fun tabIndexOrDefault(route: NavigationRoute, fallbackIndex: Int): Int {
    val index = NavigationRoute.entries.indexOf(route)
    return if (index >= 0) index else fallbackIndex
}

private fun calculateInitialPagerPage(tabCount: Int, startRealPage: Int): Int {
    if (tabCount <= 0) return 0
    val mid = Int.MAX_VALUE / 2
    return mid - mid.floorMod(tabCount) + startRealPage.floorMod(tabCount)
}

private fun calculateNearestPagerPage(
    currentPage: Int,
    currentRealPage: Int,
    targetRealPage: Int,
    tabCount: Int
): Int {
    if (tabCount <= 0) return currentPage
    val forwardDelta = (targetRealPage - currentRealPage + tabCount) % tabCount
    val backwardDelta = forwardDelta - tabCount
    val shortestDelta = if (abs(forwardDelta) <= abs(backwardDelta)) forwardDelta else backwardDelta
    return currentPage + shortestDelta
}

private fun Int.floorMod(divisor: Int): Int {
    if (divisor == 0) return 0
    return ((this % divisor) + divisor) % divisor
}
