package de.leipsfur.kcal_track

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.leipsfur.kcal_track.ui.navigation.KcalTrackNavHost
import de.leipsfur.kcal_track.ui.navigation.NavigationRoute
import de.leipsfur.kcal_track.ui.theme.KcaltrackTheme

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
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationRoute.entries.forEach { route ->
                    NavigationBarItem(
                        selected = currentRoute == route.route,
                        onClick = {
                            navController.navigate(route.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = route.icon,
                                contentDescription = stringResource(route.labelResId)
                            )
                        },
                        label = { Text(stringResource(route.labelResId)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        KcalTrackNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            openQuickAdd = openQuickAdd
        )
    }
}