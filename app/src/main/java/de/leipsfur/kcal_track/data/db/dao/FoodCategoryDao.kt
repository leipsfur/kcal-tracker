package de.leipsfur.kcal_track.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodCategoryDao {
    @Query("SELECT * FROM food_category ORDER BY sortOrder ASC")
    fun getAll(): Flow<List<FoodCategory>>

    @Query("SELECT * FROM food_category WHERE id = :id")
    suspend fun getById(id: Long): FoodCategory?

    @Insert
    suspend fun insert(category: FoodCategory): Long

    @Update
    suspend fun update(category: FoodCategory)

    @Delete
    suspend fun delete(category: FoodCategory)

    @Query(
        """
        SELECT COUNT(*) FROM (
            SELECT id FROM food_template WHERE category_id = :categoryId
            UNION ALL
            SELECT id FROM food_entry WHERE category_id = :categoryId
        )
        """
    )
    suspend fun getUsageCount(categoryId: Long): Int

    @Query("SELECT MAX(sortOrder) FROM food_category")
    suspend fun getMaxSortOrder(): Int?
}
