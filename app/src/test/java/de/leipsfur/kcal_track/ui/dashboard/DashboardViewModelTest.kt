package de.leipsfur.kcal_track.ui.dashboard

import de.leipsfur.kcal_track.MainDispatcherRule
import de.leipsfur.kcal_track.data.db.entity.ActivityEntry
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import de.leipsfur.kcal_track.data.db.entity.UserSettings
import de.leipsfur.kcal_track.data.repository.ActivityRepository
import de.leipsfur.kcal_track.data.repository.FoodRepository
import de.leipsfur.kcal_track.data.repository.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val foodRepository = mockk<FoodRepository>()
    private val activityRepository = mockk<ActivityRepository>()
    private val settingsRepository = mockk<SettingsRepository>()
    private val dateFlow = MutableStateFlow(LocalDate.now())
    private val onDateChangedCallback = mockk<(LocalDate) -> Unit>(relaxed = true)

    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setup() {
        every { foodRepository.getAllCategories() } returns flowOf(emptyList())
        every { activityRepository.getAllCategories() } returns flowOf(emptyList())
        every { settingsRepository.getSettings() } returns flowOf(UserSettings(1, 2000))
        
        every { foodRepository.getEntriesByDate(any()) } returns flowOf(emptyList())
        every { activityRepository.getEntriesByDate(any()) } returns flowOf(emptyList())

        viewModel = DashboardViewModel(
            foodRepository,
            activityRepository,
            settingsRepository,
            dateFlow,
            onDateChangedCallback
        )
    }

    @Test
    fun `initial state calculates TDEE and remaining correctly with no entries`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        val state = viewModel.uiState.value
        
        assertEquals(2000, state.bmr)
        assertEquals(2000, state.tdee)
        assertEquals(2000, state.remainingKcal)
    }

    @Test
    fun `state updates when entries exist`() = runTest {
        val today = LocalDate.now()
        val foodEntries = listOf(
            FoodEntry(id = 1, date = today, name = "Apple", kcal = 100, amount = 1.0, categoryId = 1)
        )
        val activityEntries = listOf(
            ActivityEntry(id = 1, date = today, name = "Run", kcal = 300, categoryId = 1)
        )

        every { foodRepository.getEntriesByDate(today) } returns flowOf(foodEntries)
        every { activityRepository.getEntriesByDate(today) } returns flowOf(activityEntries)

        // Re-init VM to pick up mocks (or update mocks if flow is collected dynamically? 
        // flatMapLatest reacts to date change. If I want to test data change for same date, 
        // I need to emit new flow or use MutableSharedFlow in mock.
        // But here I defined mocks before VM init. 
        // Wait, setup() runs before every test.
        // So I should define specific mocks inside this test BEFORE creating VM?
        // Or override mocks?
        // Since setup() creates VM, I need to recreate VM here or set mocks in setup but conditional?
        // I'll recreate VM.
        
        viewModel = DashboardViewModel(
            foodRepository,
            activityRepository,
            settingsRepository,
            dateFlow,
            onDateChangedCallback
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        val state = viewModel.uiState.value
        
        // BMR 2000 + Activity 300 = TDEE 2300
        // Intake 100
        // Remaining 2200
        assertEquals(2000, state.bmr)
        assertEquals(2300, state.tdee)
        assertEquals(100, state.totalFoodKcal)
        assertEquals(300, state.totalActivityKcal)
        assertEquals(2200, state.remainingKcal)
    }

    @Test
    fun `onDateChanged calls callback`() {
        val newDate = LocalDate.now().plusDays(1)
        viewModel.onDateChanged(newDate)
        
        verify { onDateChangedCallback(newDate) }
    }
}
