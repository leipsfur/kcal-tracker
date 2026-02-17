package de.leipsfur.kcal_track.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.leipsfur.kcal_track.data.db.entity.FoodTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodTemplateDao {
    @Query("SELECT * FROM food_template ORDER BY name ASC")
    fun getAll(): Flow<List<FoodTemplate>>

    @Query("SELECT * FROM food_template WHERE id = :id")
    suspend fun getById(id: Long): FoodTemplate?

    @Query("SELECT * FROM food_template WHERE category_id = :categoryId ORDER BY name ASC")
    fun getByCategoryId(categoryId: Long): Flow<List<FoodTemplate>>

    @Query("SELECT * FROM food_template WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): Flow<List<FoodTemplate>>

    @Insert
    suspend fun insert(template: FoodTemplate): Long

    @Update
    suspend fun update(template: FoodTemplate)

    @Delete
    suspend fun delete(template: FoodTemplate)
}
