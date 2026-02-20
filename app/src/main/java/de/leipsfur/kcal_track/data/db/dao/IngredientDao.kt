package de.leipsfur.kcal_track.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.leipsfur.kcal_track.data.db.entity.Ingredient
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Query("SELECT * FROM ingredient ORDER BY name ASC")
    fun getAll(): Flow<List<Ingredient>>

    @Query("SELECT * FROM ingredient WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): Flow<List<Ingredient>>

    @Query("SELECT * FROM ingredient WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): Ingredient?

    @Insert
    suspend fun insert(ingredient: Ingredient): Long

    @Update
    suspend fun update(ingredient: Ingredient)

    @Delete
    suspend fun delete(ingredient: Ingredient)
}
