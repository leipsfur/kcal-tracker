package de.leipsfur.kcal_track.data.repository

import de.leipsfur.kcal_track.data.db.dao.FoodCategoryDao
import de.leipsfur.kcal_track.data.db.dao.FoodEntryDao
import de.leipsfur.kcal_track.data.db.dao.FoodTemplateDao
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import de.leipsfur.kcal_track.data.db.entity.FoodTemplate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class FoodRepositoryTest {

    private val foodTemplateDao = mockk<FoodTemplateDao>()
    private val foodEntryDao = mockk<FoodEntryDao>()
    private val foodCategoryDao = mockk<FoodCategoryDao>()

    private lateinit var repository: FoodRepository

    @Before
    fun setup() {
        repository = FoodRepository(foodTemplateDao, foodEntryDao, foodCategoryDao)
    }

    @Test
    fun templateMethods_delegateToTemplateDao() = runTest {
        val template = FoodTemplate(
            id = 10,
            name = "Quark",
            kcal = 120,
            categoryId = 1,
            portionSize = 100.0,
            portionUnit = "g"
        )
        val templates = listOf(template)

        every { foodTemplateDao.getAll() } returns flowOf(templates)
        coEvery { foodTemplateDao.getById(10) } returns template
        every { foodTemplateDao.getByCategoryId(1) } returns flowOf(templates)
        every { foodTemplateDao.search("Qua") } returns flowOf(templates)
        coEvery { foodTemplateDao.insert(template) } returns 42L
        coEvery { foodTemplateDao.update(template) } returns Unit
        coEvery { foodTemplateDao.delete(template) } returns Unit

        assertEquals(templates, repository.getAllTemplates().first())
        assertEquals(template, repository.getTemplateById(10))
        assertEquals(templates, repository.getTemplatesByCategoryId(1).first())
        assertEquals(templates, repository.searchTemplates("Qua").first())
        assertEquals(42L, repository.insertTemplate(template))
        repository.updateTemplate(template)
        repository.deleteTemplate(template)

        coVerify(exactly = 1) { foodTemplateDao.getById(10) }
        coVerify(exactly = 1) { foodTemplateDao.insert(template) }
        coVerify(exactly = 1) { foodTemplateDao.update(template) }
        coVerify(exactly = 1) { foodTemplateDao.delete(template) }
    }

    @Test
    fun entryMethods_delegateToEntryDao() = runTest {
        val date = LocalDate.of(2026, 2, 17)
        val entry = FoodEntry(
            id = 1,
            date = date,
            name = "Banane",
            kcal = 100,
            amount = 1.0,
            categoryId = 1,
            time = "12:00"
        )
        val entries = listOf(entry)

        every { foodEntryDao.getByDate(date) } returns flowOf(entries)
        coEvery { foodEntryDao.getById(1) } returns entry
        every { foodEntryDao.getTotalKcalForDate(date) } returns flowOf(100)
        coEvery { foodEntryDao.insert(entry) } returns 1L
        coEvery { foodEntryDao.update(entry) } returns Unit
        coEvery { foodEntryDao.delete(entry) } returns Unit

        assertEquals(entries, repository.getEntriesByDate(date).first())
        assertEquals(entry, repository.getEntryById(1))
        assertEquals(100, repository.getTotalKcalForDate(date).first())
        assertEquals(1L, repository.insertEntry(entry))
        repository.updateEntry(entry)
        repository.deleteEntry(entry)

        coVerify(exactly = 1) { foodEntryDao.getById(1) }
        coVerify(exactly = 1) { foodEntryDao.insert(entry) }
        coVerify(exactly = 1) { foodEntryDao.update(entry) }
        coVerify(exactly = 1) { foodEntryDao.delete(entry) }
    }

    @Test
    fun categoryMethods_delegateToCategoryDao() = runTest {
        val category = FoodCategory(id = 1, name = "Frühstück", sortOrder = 0)
        val categories = listOf(category)

        every { foodCategoryDao.getAll() } returns flowOf(categories)
        coEvery { foodCategoryDao.getById(1) } returns category
        coEvery { foodCategoryDao.insert(category) } returns 1L
        coEvery { foodCategoryDao.update(category) } returns Unit
        coEvery { foodCategoryDao.delete(category) } returns Unit
        coEvery { foodCategoryDao.getUsageCount(1) } returns 3
        coEvery { foodCategoryDao.getMaxSortOrder() } returns 5

        assertEquals(categories, repository.getAllCategories().first())
        assertEquals(category, repository.getCategoryById(1))
        assertEquals(1L, repository.insertCategory(category))
        repository.updateCategory(category)
        repository.deleteCategory(category)
        assertEquals(3, repository.getCategoryUsageCount(1))
        assertEquals(5, repository.getMaxCategorySortOrder())

        coVerify(exactly = 1) { foodCategoryDao.getById(1) }
        coVerify(exactly = 1) { foodCategoryDao.insert(category) }
        coVerify(exactly = 1) { foodCategoryDao.update(category) }
        coVerify(exactly = 1) { foodCategoryDao.delete(category) }
        coVerify(exactly = 1) { foodCategoryDao.getUsageCount(1) }
        coVerify(exactly = 1) { foodCategoryDao.getMaxSortOrder() }
    }
}
