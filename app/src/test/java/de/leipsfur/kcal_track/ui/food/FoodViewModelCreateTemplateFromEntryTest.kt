package de.leipsfur.kcal_track.ui.food

import de.leipsfur.kcal_track.MainDispatcherRule
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import de.leipsfur.kcal_track.data.repository.FoodRepository
import de.leipsfur.kcal_track.domain.model.PortionUnit
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class FoodViewModelCreateTemplateFromEntryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<FoodRepository>()
    private val dateFlow = MutableStateFlow(LocalDate.of(2026, 2, 18))
    private val onDateChanged = mockk<(LocalDate) -> Unit>(relaxed = true)
    private val onDataChanged = mockk<() -> Unit>(relaxed = true)

    private val testCategory = FoodCategory(id = 1, name = "Frühstück", sortOrder = 0)

    @Before
    fun setup() {
        every { repository.getEntriesByDate(any()) } returns flowOf(emptyList())
        every { repository.getAllTemplates() } returns flowOf(emptyList())
        every { repository.getAllCategories() } returns flowOf(listOf(testCategory))
    }

    private fun createViewModel() = FoodViewModel(
        foodRepository = repository,
        dateFlow = dateFlow,
        onDateChangedCallback = onDateChanged,
        onDataChanged = onDataChanged
    )

    @Test
    fun showCreateTemplateFromEntry_prefillsDialogWithEntryData() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val entry = FoodEntry(
            id = 42,
            date = LocalDate.of(2026, 2, 18),
            name = "Skyr Natur",
            kcal = 150,
            protein = 12.0,
            carbs = 4.5,
            fat = 0.2,
            amount = 200.0,
            portionUnit = PortionUnit.GRAM,
            categoryId = 1,
            time = "12:00"
        )

        viewModel.showCreateTemplateFromEntry(entry)
        val state = viewModel.uiState.value

        assertTrue(state.showTemplateDialog)
        assertNull(state.editingTemplate)
        assertEquals("Skyr Natur", state.templateName)
        assertEquals("150", state.templateKcal)
        assertEquals("12.0", state.templateProtein)
        assertEquals("4.5", state.templateCarbs)
        assertEquals("0.2", state.templateFat)
        assertEquals("200.0", state.templatePortionSize)
        assertEquals(PortionUnit.GRAM, state.templatePortionUnit)
        assertEquals(1L, state.templateCategoryId)
        assertNull(state.templateValidationError)
    }

    @Test
    fun saveTemplate_afterCreateFromEntry_insertsNewTemplate() = runTest {
        coEvery { repository.insertTemplate(any()) } returns 1L

        val viewModel = createViewModel()
        advanceUntilIdle()

        val entry = FoodEntry(
            id = 42,
            date = LocalDate.of(2026, 2, 18),
            name = "Skyr Natur",
            kcal = 150,
            protein = 12.0,
            carbs = 4.5,
            fat = 0.2,
            amount = 200.0,
            portionUnit = PortionUnit.GRAM,
            categoryId = 1,
            time = "12:00"
        )

        viewModel.showCreateTemplateFromEntry(entry)
        viewModel.saveTemplate()
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.insertTemplate(any()) }
        coVerify(exactly = 0) { repository.updateTemplate(any()) }
    }

    @Test
    fun showCreateTemplateFromEntry_nullPortionUnit_fallsBackToGram() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val entry = FoodEntry(
            id = 43,
            date = LocalDate.of(2026, 2, 18),
            name = "Apfel",
            kcal = 80,
            amount = 1.0,
            portionUnit = null,
            categoryId = 1,
            time = "12:00"
        )

        viewModel.showCreateTemplateFromEntry(entry)
        val state = viewModel.uiState.value

        assertEquals(PortionUnit.GRAM, state.templatePortionUnit)
    }
}
