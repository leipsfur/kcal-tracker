package de.leipsfur.kcal_track.ui.food

import de.leipsfur.kcal_track.MainDispatcherRule
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.repository.FoodRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class FoodViewModelPerformanceGuardTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<FoodRepository>()
    private val dateFlow = MutableStateFlow(LocalDate.of(2026, 2, 17))
    private val onDateChanged = mockk<(LocalDate) -> Unit>(relaxed = true)
    private val onDataChanged = mockk<() -> Unit>(relaxed = true)

    @Before
    fun setup() {
        every { repository.getEntriesByDate(any()) } returns flowOf(emptyList())
        every { repository.getAllTemplates() } returns flowOf(emptyList())
        every {
            repository.getAllCategories()
        } returns flowOf(listOf(FoodCategory(id = 1, name = "Test", sortOrder = 0)))

        coEvery { repository.insertEntry(any()) } returns 1L
    }

    @Test
    fun saveEntry_triggersOnDataChangedExactlyOnce() = runTest {
        val viewModel = FoodViewModel(
            foodRepository = repository,
            dateFlow = dateFlow,
            onDateChangedCallback = onDateChanged,
            onDataChanged = onDataChanged
        )
        advanceUntilIdle()

        viewModel.showManualEntryDialog()
        viewModel.onEntryNameChanged("Skyr")
        viewModel.onEntryKcalChanged("150")
        viewModel.saveEntry()

        advanceUntilIdle()

        coVerify(exactly = 1) { repository.insertEntry(any()) }
        verify(exactly = 1) { onDataChanged.invoke() }
    }
}
