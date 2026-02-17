package de.leipsfur.kcal_track.ui.dashboard

import de.leipsfur.kcal_track.MainDispatcherRule
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import de.leipsfur.kcal_track.data.db.entity.UserSettings
import de.leipsfur.kcal_track.data.repository.ActivityRepository
import de.leipsfur.kcal_track.data.repository.FoodRepository
import de.leipsfur.kcal_track.data.repository.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardEdgeCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val foodRepository = mockk<FoodRepository>()
    private val activityRepository = mockk<ActivityRepository>()
    private val settingsRepository = mockk<SettingsRepository>()
    private val onDateChangedCallback = mockk<(LocalDate) -> Unit>(relaxed = true)

    @Before
    fun setup() {
        every { foodRepository.getAllCategories() } returns flowOf(emptyList())
        every { activityRepository.getAllCategories() } returns flowOf(emptyList())
        every { activityRepository.getEntriesByDate(any()) } returns flowOf(emptyList())
    }

    @Test
    fun noBmr_withIntake_resultsInNegativeRemaining() = runTest {
        val dateFlow = MutableStateFlow(LocalDate.of(2026, 2, 17))
        val foodEntries = listOf(
            FoodEntry(
                id = 1,
                date = dateFlow.value,
                name = "Pizza",
                kcal = 800,
                amount = 1.0,
                categoryId = 1
            )
        )

        every { settingsRepository.getSettings() } returns flowOf(null)
        every { foodRepository.getEntriesByDate(any()) } returns flowOf(foodEntries)

        val viewModel = DashboardViewModel(
            foodRepository,
            activityRepository,
            settingsRepository,
            dateFlow,
            onDateChangedCallback
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(null, state.bmr)
        assertEquals(0, state.tdee)
        assertEquals(-800, state.remainingKcal)
    }

    @Test
    fun dateFlowChange_switchesSelectedDateAndLoadsDifferentEntries() = runTest {
        val today = LocalDate.of(2026, 2, 17)
        val yesterday = today.minusDays(1)
        val dateFlow = MutableStateFlow(today)

        every { settingsRepository.getSettings() } returns flowOf(UserSettings(id = 1, bmr = 2000))
        every { foodRepository.getEntriesByDate(any()) } answers {
            val requested = firstArg<LocalDate>()
            if (requested == today) {
                flowOf(
                    listOf(
                        FoodEntry(
                            id = 1,
                            date = today,
                            name = "Haferflocken",
                            kcal = 350,
                            amount = 1.0,
                            categoryId = 1
                        )
                    )
                )
            } else {
                flowOf(
                    listOf(
                        FoodEntry(
                            id = 2,
                            date = yesterday,
                            name = "Nudeln",
                            kcal = 600,
                            amount = 1.0,
                            categoryId = 1
                        )
                    )
                )
            }
        }

        val viewModel = DashboardViewModel(
            foodRepository,
            activityRepository,
            settingsRepository,
            dateFlow,
            onDateChangedCallback
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        advanceUntilIdle()
        assertEquals(today, viewModel.uiState.value.selectedDate)
        assertEquals(350, viewModel.uiState.value.totalFoodKcal)

        dateFlow.value = yesterday
        advanceUntilIdle()

        assertEquals(yesterday, viewModel.uiState.value.selectedDate)
        assertEquals(600, viewModel.uiState.value.totalFoodKcal)
    }
}
