package de.leipsfur.kcal_track.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.leipsfur.kcal_track.data.db.entity.RecipeIngredient
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeIngredientDao {
    @Query("SELECT * FROM recipe_ingredient WHERE recipe_id = :recipeId ORDER BY id ASC")
    fun getByRecipeId(recipeId: Long): Flow<List<RecipeIngredient>>

    @Query("SELECT * FROM recipe_ingredient WHERE recipe_id = :recipeId ORDER BY id ASC")
    suspend fun getByRecipeIdOnce(recipeId: Long): List<RecipeIngredient>

    @Insert
    suspend fun insert(ingredient: RecipeIngredient): Long

    @Update
    suspend fun update(ingredient: RecipeIngredient)

    @Delete
    suspend fun delete(ingredient: RecipeIngredient)

    @Query("DELETE FROM recipe_ingredient WHERE recipe_id = :recipeId")
    suspend fun deleteByRecipeId(recipeId: Long)
}
