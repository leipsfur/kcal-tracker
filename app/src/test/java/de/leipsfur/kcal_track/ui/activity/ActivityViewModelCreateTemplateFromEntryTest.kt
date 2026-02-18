package de.leipsfur.kcal_track.ui.activity

import de.leipsfur.kcal_track.MainDispatcherRule
import de.leipsfur.kcal_track.data.db.entity.ActivityCategory
import de.leipsfur.kcal_track.data.db.entity.ActivityEntry
import de.leipsfur.kcal_track.data.repository.ActivityRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityViewModelCreateTemplateFromEntryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<ActivityRepository>()
    private val dateFlow = MutableStateFlow(LocalDate.of(2026, 2, 18))
    private val onDateChanged = mockk<(LocalDate) -> Unit>(relaxed = true)
    private val onDataChanged = mockk<() -> Unit>(relaxed = true)

    private val testCategory = ActivityCategory(id = 1, name = "Cardio", sortOrder = 0)

    @Before
    fun setup() {
        every { repository.getEntriesByDate(any()) } returns flowOf(emptyList())
        every { repository.getAllTemplates() } returns flowOf(emptyList())
        every { repository.getAllCategories() } returns flowOf(listOf(testCategory))
    }

    private fun createViewModel() = ActivityViewModel(
        activityRepository = repository,
        dateFlow = dateFlow,
        onDateChangedCallback = onDateChanged,
        onDataChanged = onDataChanged
    )

    @Test
    fun showCreateTemplateFromEntry_prefillsDialogWithEntryData() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        val entry = ActivityEntry(
            id = 10,
            date = LocalDate.of(2026, 2, 18),
            name = "Joggen 30min",
            kcal = 300,
            categoryId = 1
        )

        viewModel.showCreateTemplateFromEntry(entry)
        advanceUntilIdle()

        val dialog = viewModel.uiState.value.templateDialog
        assertNotNull(dialog)
        assertNull(dialog!!.editingTemplate)
        assertEquals("Joggen 30min", dialog.name)
        assertEquals("300", dialog.kcal)
        assertEquals(1L, dialog.categoryId)
    }

    @Test
    fun saveTemplate_afterCreateFromEntry_insertsNewTemplate() = runTest {
        coEvery { repository.insertTemplate(any()) } returns 1L

        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        val entry = ActivityEntry(
            id = 10,
            date = LocalDate.of(2026, 2, 18),
            name = "Joggen 30min",
            kcal = 300,
            categoryId = 1
        )

        viewModel.showCreateTemplateFromEntry(entry)
        viewModel.saveTemplate()
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.insertTemplate(any()) }
        coVerify(exactly = 0) { repository.updateTemplate(any()) }
    }
}
