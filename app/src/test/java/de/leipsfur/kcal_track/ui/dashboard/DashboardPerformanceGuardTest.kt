package de.leipsfur.kcal_track.ui.dashboard

import de.leipsfur.kcal_track.data.db.entity.ActivityEntry
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DashboardPerformanceGuardTest {

    @Test(timeout = 2000)
    fun largeDailyData_set_computesTotalsWithinTimeout() {
        val date = LocalDate.of(2026, 2, 17)
        val foodEntries = (1..1000).map {
            FoodEntry(
                id = it.toLong(),
                date = date,
                name = "Food $it",
                kcal = 5,
                amount = 1.0,
                categoryId = 1
            )
        }
        val activityEntries = (1..1000).map {
            ActivityEntry(
                id = it.toLong(),
                date = date,
                name = "Activity $it",
                kcal = 3,
                categoryId = 1
            )
        }

        val state = DashboardUiState(
            selectedDate = date,
            bmr = 2000,
            foodEntries = foodEntries,
            activityEntries = activityEntries
        )

        assertEquals(5000, state.totalFoodKcal)
        assertEquals(3000, state.totalActivityKcal)
        assertEquals(5000, state.tdee)
        assertEquals(0, state.remainingKcal)
    }
}
