package de.leipsfur.kcal_track.data.repository

import de.leipsfur.kcal_track.data.db.dao.FoodCategoryDao
import de.leipsfur.kcal_track.data.db.dao.FoodEntryDao
import de.leipsfur.kcal_track.data.db.dao.FoodTemplateDao
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import de.leipsfur.kcal_track.data.db.entity.FoodTemplate
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class FoodRepository(
    private val foodTemplateDao: FoodTemplateDao,
    private val foodEntryDao: FoodEntryDao,
    private val foodCategoryDao: FoodCategoryDao
) {
    // Templates
    fun getAllTemplates(): Flow<List<FoodTemplate>> = foodTemplateDao.getAll()

    suspend fun getTemplateById(id: Long): FoodTemplate? = foodTemplateDao.getById(id)

    fun getTemplatesByCategoryId(categoryId: Long): Flow<List<FoodTemplate>> =
        foodTemplateDao.getByCategoryId(categoryId)

    fun searchTemplates(query: String): Flow<List<FoodTemplate>> = foodTemplateDao.search(query)

    suspend fun insertTemplate(template: FoodTemplate): Long = foodTemplateDao.insert(template)

    suspend fun updateTemplate(template: FoodTemplate) = foodTemplateDao.update(template)

    suspend fun deleteTemplate(template: FoodTemplate) = foodTemplateDao.delete(template)

    // Entries
    fun getEntriesByDate(date: LocalDate): Flow<List<FoodEntry>> = foodEntryDao.getByDate(date)

    suspend fun getEntryById(id: Long): FoodEntry? = foodEntryDao.getById(id)

    fun getTotalKcalForDate(date: LocalDate): Flow<Int> = foodEntryDao.getTotalKcalForDate(date)

    suspend fun insertEntry(entry: FoodEntry): Long = foodEntryDao.insert(entry)

    suspend fun updateEntry(entry: FoodEntry) = foodEntryDao.update(entry)

    suspend fun deleteEntry(entry: FoodEntry) = foodEntryDao.delete(entry)

    // Categories
    fun getAllCategories(): Flow<List<FoodCategory>> = foodCategoryDao.getAll()

    suspend fun getCategoryById(id: Long): FoodCategory? = foodCategoryDao.getById(id)

    suspend fun insertCategory(category: FoodCategory): Long = foodCategoryDao.insert(category)

    suspend fun updateCategory(category: FoodCategory) = foodCategoryDao.update(category)

    suspend fun deleteCategory(category: FoodCategory) = foodCategoryDao.delete(category)

    suspend fun getCategoryUsageCount(categoryId: Long): Int =
        foodCategoryDao.getUsageCount(categoryId)

    suspend fun getMaxCategorySortOrder(): Int? = foodCategoryDao.getMaxSortOrder()
}
