package de.leipsfur.kcal_track.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.leipsfur.kcal_track.data.db.entity.ActivityCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityCategoryDao {
    @Query("SELECT * FROM activity_category ORDER BY sortOrder ASC")
    fun getAll(): Flow<List<ActivityCategory>>

    @Query("SELECT * FROM activity_category WHERE id = :id")
    suspend fun getById(id: Long): ActivityCategory?

    @Insert
    suspend fun insert(category: ActivityCategory): Long

    @Update
    suspend fun update(category: ActivityCategory)

    @Delete
    suspend fun delete(category: ActivityCategory)

    @Query(
        """
        SELECT COUNT(*) FROM (
            SELECT id FROM activity_template WHERE category_id = :categoryId
            UNION ALL
            SELECT id FROM activity_entry WHERE category_id = :categoryId
        )
        """
    )
    suspend fun getUsageCount(categoryId: Long): Int

    @Query("SELECT MAX(sortOrder) FROM activity_category")
    suspend fun getMaxSortOrder(): Int?
}
