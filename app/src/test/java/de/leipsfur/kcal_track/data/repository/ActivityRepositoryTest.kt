package de.leipsfur.kcal_track.data.repository

import de.leipsfur.kcal_track.data.db.dao.ActivityCategoryDao
import de.leipsfur.kcal_track.data.db.dao.ActivityEntryDao
import de.leipsfur.kcal_track.data.db.dao.ActivityTemplateDao
import de.leipsfur.kcal_track.data.db.entity.ActivityCategory
import de.leipsfur.kcal_track.data.db.entity.ActivityEntry
import de.leipsfur.kcal_track.data.db.entity.ActivityTemplate
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

class ActivityRepositoryTest {

    private val activityTemplateDao = mockk<ActivityTemplateDao>()
    private val activityEntryDao = mockk<ActivityEntryDao>()
    private val activityCategoryDao = mockk<ActivityCategoryDao>()

    private lateinit var repository: ActivityRepository

    @Before
    fun setup() {
        repository = ActivityRepository(activityTemplateDao, activityEntryDao, activityCategoryDao)
    }

    @Test
    fun templateMethods_delegateToTemplateDao() = runTest {
        val template = ActivityTemplate(id = 10, name = "Laufen", kcal = 300, categoryId = 1)
        val templates = listOf(template)

        every { activityTemplateDao.getAll() } returns flowOf(templates)
        coEvery { activityTemplateDao.getById(10) } returns template
        every { activityTemplateDao.getByCategoryId(1) } returns flowOf(templates)
        every { activityTemplateDao.search("Lauf") } returns flowOf(templates)
        coEvery { activityTemplateDao.insert(template) } returns 44L
        coEvery { activityTemplateDao.update(template) } returns Unit
        coEvery { activityTemplateDao.delete(template) } returns Unit

        assertEquals(templates, repository.getAllTemplates().first())
        assertEquals(template, repository.getTemplateById(10))
        assertEquals(templates, repository.getTemplatesByCategoryId(1).first())
        assertEquals(templates, repository.searchTemplates("Lauf").first())
        assertEquals(44L, repository.insertTemplate(template))
        repository.updateTemplate(template)
        repository.deleteTemplate(template)

        coVerify(exactly = 1) { activityTemplateDao.getById(10) }
        coVerify(exactly = 1) { activityTemplateDao.insert(template) }
        coVerify(exactly = 1) { activityTemplateDao.update(template) }
        coVerify(exactly = 1) { activityTemplateDao.delete(template) }
    }

    @Test
    fun entryMethods_delegateToEntryDao() = runTest {
        val date = LocalDate.of(2026, 2, 17)
        val entry = ActivityEntry(
            id = 1,
            date = date,
            name = "Fahrrad",
            kcal = 250,
            categoryId = 1
        )
        val entries = listOf(entry)

        every { activityEntryDao.getByDate(date) } returns flowOf(entries)
        coEvery { activityEntryDao.getById(1) } returns entry
        every { activityEntryDao.getTotalKcalForDate(date) } returns flowOf(250)
        coEvery { activityEntryDao.insert(entry) } returns 1L
        coEvery { activityEntryDao.update(entry) } returns Unit
        coEvery { activityEntryDao.delete(entry) } returns Unit

        assertEquals(entries, repository.getEntriesByDate(date).first())
        assertEquals(entry, repository.getEntryById(1))
        assertEquals(250, repository.getTotalKcalForDate(date).first())
        assertEquals(1L, repository.insertEntry(entry))
        repository.updateEntry(entry)
        repository.deleteEntry(entry)

        coVerify(exactly = 1) { activityEntryDao.getById(1) }
        coVerify(exactly = 1) { activityEntryDao.insert(entry) }
        coVerify(exactly = 1) { activityEntryDao.update(entry) }
        coVerify(exactly = 1) { activityEntryDao.delete(entry) }
    }

    @Test
    fun categoryMethods_delegateToCategoryDao() = runTest {
        val category = ActivityCategory(id = 1, name = "Cardio", sortOrder = 0)
        val categories = listOf(category)

        every { activityCategoryDao.getAll() } returns flowOf(categories)
        coEvery { activityCategoryDao.getById(1) } returns category
        coEvery { activityCategoryDao.insert(category) } returns 1L
        coEvery { activityCategoryDao.update(category) } returns Unit
        coEvery { activityCategoryDao.delete(category) } returns Unit
        coEvery { activityCategoryDao.getUsageCount(1) } returns 2
        coEvery { activityCategoryDao.getMaxSortOrder() } returns 7

        assertEquals(categories, repository.getAllCategories().first())
        assertEquals(category, repository.getCategoryById(1))
        assertEquals(1L, repository.insertCategory(category))
        repository.updateCategory(category)
        repository.deleteCategory(category)
        assertEquals(2, repository.getCategoryUsageCount(1))
        assertEquals(7, repository.getMaxCategorySortOrder())

        coVerify(exactly = 1) { activityCategoryDao.getById(1) }
        coVerify(exactly = 1) { activityCategoryDao.insert(category) }
        coVerify(exactly = 1) { activityCategoryDao.update(category) }
        coVerify(exactly = 1) { activityCategoryDao.delete(category) }
        coVerify(exactly = 1) { activityCategoryDao.getUsageCount(1) }
        coVerify(exactly = 1) { activityCategoryDao.getMaxSortOrder() }
    }
}
