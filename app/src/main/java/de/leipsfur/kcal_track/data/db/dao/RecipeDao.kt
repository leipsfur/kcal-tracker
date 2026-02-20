package de.leipsfur.kcal_track.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.leipsfur.kcal_track.data.db.entity.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe ORDER BY created_date DESC, id DESC")
    fun getAll(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE id = :id")
    suspend fun getById(id: Long): Recipe?

    @Query("SELECT * FROM recipe WHERE status = :status ORDER BY created_date DESC")
    fun getByStatus(status: String): Flow<List<Recipe>>

    @Insert
    suspend fun insert(recipe: Recipe): Long

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)
}
