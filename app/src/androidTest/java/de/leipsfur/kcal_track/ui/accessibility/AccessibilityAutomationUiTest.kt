package de.leipsfur.kcal_track.ui.accessibility

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.leipsfur.kcal_track.ui.dashboard.DateNavigation
import de.leipsfur.kcal_track.ui.theme.KcaltrackTheme
import de.leipsfur.kcal_track.ui.weight.WeightChart
import de.leipsfur.kcal_track.data.db.entity.WeightEntry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class AccessibilityAutomationUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun dateNavigation_hasAccessibleContentDescriptions() {
        composeRule.setContent {
            KcaltrackTheme {
                DateNavigation(
                    selectedDate = LocalDate.of(2026, 2, 17),
                    onDateChanged = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription("Vorheriger Tag").assertExists()
        composeRule.onNodeWithContentDescription("Nächster Tag").assertExists()
    }

    @Test
    fun weightChart_exposesSemanticDescription() {
        composeRule.setContent {
            KcaltrackTheme {
                WeightChart(
                    entries = listOf(
                        WeightEntry(
                            id = 1,
                            date = LocalDate.of(2026, 2, 16),
                            weightKg = 80.5
                        ),
                        WeightEntry(
                            id = 2,
                            date = LocalDate.of(2026, 2, 17),
                            weightKg = 80.1
                        )
                    )
                )
            }
        }

        composeRule.onNodeWithContentDescription("Gewichtsverlauf Diagramm").assertExists()
    }
}
