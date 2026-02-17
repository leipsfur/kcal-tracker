package de.leipsfur.kcal_track.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.leipsfur.kcal_track.data.db.entity.ActivityTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityTemplateDao {
    @Query("SELECT * FROM activity_template ORDER BY name ASC")
    fun getAll(): Flow<List<ActivityTemplate>>

    @Query("SELECT * FROM activity_template WHERE id = :id")
    suspend fun getById(id: Long): ActivityTemplate?

    @Query("SELECT * FROM activity_template WHERE category_id = :categoryId ORDER BY name ASC")
    fun getByCategoryId(categoryId: Long): Flow<List<ActivityTemplate>>

    @Query("SELECT * FROM activity_template WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): Flow<List<ActivityTemplate>>

    @Insert
    suspend fun insert(template: ActivityTemplate): Long

    @Update
    suspend fun update(template: ActivityTemplate)

    @Delete
    suspend fun delete(template: ActivityTemplate)
}
