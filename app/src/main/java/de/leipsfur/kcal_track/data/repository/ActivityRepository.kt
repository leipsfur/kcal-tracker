package de.leipsfur.kcal_track.data.repository

import de.leipsfur.kcal_track.data.db.dao.ActivityCategoryDao
import de.leipsfur.kcal_track.data.db.dao.ActivityEntryDao
import de.leipsfur.kcal_track.data.db.dao.ActivityTemplateDao
import de.leipsfur.kcal_track.data.db.entity.ActivityCategory
import de.leipsfur.kcal_track.data.db.entity.ActivityEntry
import de.leipsfur.kcal_track.data.db.entity.ActivityTemplate
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class ActivityRepository(
    private val activityTemplateDao: ActivityTemplateDao,
    private val activityEntryDao: ActivityEntryDao,
    private val activityCategoryDao: ActivityCategoryDao
) {
    // Templates
    fun getAllTemplates(): Flow<List<ActivityTemplate>> = activityTemplateDao.getAll()

    suspend fun getTemplateById(id: Long): ActivityTemplate? = activityTemplateDao.getById(id)

    fun getTemplatesByCategoryId(categoryId: Long): Flow<List<ActivityTemplate>> =
        activityTemplateDao.getByCategoryId(categoryId)

    fun searchTemplates(query: String): Flow<List<ActivityTemplate>> =
        activityTemplateDao.search(query)

    suspend fun insertTemplate(template: ActivityTemplate): Long =
        activityTemplateDao.insert(template)

    suspend fun updateTemplate(template: ActivityTemplate) = activityTemplateDao.update(template)

    suspend fun deleteTemplate(template: ActivityTemplate) = activityTemplateDao.delete(template)

    // Entries
    fun getEntriesByDate(date: LocalDate): Flow<List<ActivityEntry>> =
        activityEntryDao.getByDate(date)

    suspend fun getEntryById(id: Long): ActivityEntry? = activityEntryDao.getById(id)

    fun getTotalKcalForDate(date: LocalDate): Flow<Int> =
        activityEntryDao.getTotalKcalForDate(date)

    suspend fun insertEntry(entry: ActivityEntry): Long = activityEntryDao.insert(entry)

    suspend fun deleteEntry(entry: ActivityEntry) = activityEntryDao.delete(entry)

    // Categories
    fun getAllCategories(): Flow<List<ActivityCategory>> = activityCategoryDao.getAll()

    suspend fun getCategoryById(id: Long): ActivityCategory? = activityCategoryDao.getById(id)

    suspend fun insertCategory(category: ActivityCategory): Long =
        activityCategoryDao.insert(category)

    suspend fun updateCategory(category: ActivityCategory) = activityCategoryDao.update(category)

    suspend fun deleteCategory(category: ActivityCategory) = activityCategoryDao.delete(category)

    suspend fun getCategoryUsageCount(categoryId: Long): Int =
        activityCategoryDao.getUsageCount(categoryId)

    suspend fun getMaxCategorySortOrder(): Int? = activityCategoryDao.getMaxSortOrder()
}
