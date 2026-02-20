package de.leipsfur.kcal_track.data.repository

import de.leipsfur.kcal_track.data.db.dao.FoodEntryDao
import de.leipsfur.kcal_track.data.db.dao.IngredientDao
import de.leipsfur.kcal_track.data.db.dao.RecipeDao
import de.leipsfur.kcal_track.data.db.dao.RecipeIngredientDao
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import de.leipsfur.kcal_track.data.db.entity.Ingredient
import de.leipsfur.kcal_track.data.db.entity.Recipe
import de.leipsfur.kcal_track.data.db.entity.RecipeIngredient
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val recipeIngredientDao: RecipeIngredientDao,
    private val ingredientDao: IngredientDao,
    private val foodEntryDao: FoodEntryDao
) {
    // Recipes
    fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAll()

    suspend fun getRecipeById(id: Long): Recipe? = recipeDao.getById(id)

    fun getRecipesByStatus(status: String): Flow<List<Recipe>> = recipeDao.getByStatus(status)

    suspend fun insertRecipe(recipe: Recipe): Long = recipeDao.insert(recipe)

    suspend fun updateRecipe(recipe: Recipe) = recipeDao.update(recipe)

    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.delete(recipe)

    // Recipe Ingredients
    fun getIngredientsByRecipeId(recipeId: Long): Flow<List<RecipeIngredient>> =
        recipeIngredientDao.getByRecipeId(recipeId)

    suspend fun getIngredientsByRecipeIdOnce(recipeId: Long): List<RecipeIngredient> =
        recipeIngredientDao.getByRecipeIdOnce(recipeId)

    suspend fun insertRecipeIngredient(ingredient: RecipeIngredient): Long =
        recipeIngredientDao.insert(ingredient)

    suspend fun updateRecipeIngredient(ingredient: RecipeIngredient) =
        recipeIngredientDao.update(ingredient)

    suspend fun deleteRecipeIngredient(ingredient: RecipeIngredient) =
        recipeIngredientDao.delete(ingredient)

    suspend fun deleteRecipeIngredientsByRecipeId(recipeId: Long) =
        recipeIngredientDao.deleteByRecipeId(recipeId)

    // Ingredients
    fun getAllIngredients(): Flow<List<Ingredient>> = ingredientDao.getAll()

    fun searchIngredients(query: String): Flow<List<Ingredient>> = ingredientDao.search(query)

    suspend fun getIngredientByName(name: String): Ingredient? = ingredientDao.getByName(name)

    suspend fun insertIngredient(ingredient: Ingredient): Long = ingredientDao.insert(ingredient)

    suspend fun updateIngredient(ingredient: Ingredient) = ingredientDao.update(ingredient)

    suspend fun deleteIngredient(ingredient: Ingredient) = ingredientDao.delete(ingredient)

    // Complex operations
    suspend fun addRecipeAsFoodEntry(
        recipe: Recipe,
        ingredients: List<RecipeIngredient>,
        eatenPortions: Double,
        categoryId: Long,
        date: LocalDate,
        time: String
    ) {
        val totalPortions = recipe.totalPortions ?: 1.0

        val totalKcal = ingredients.sumOf { it.kcalPer100 * it.amount / 100.0 }
        val totalProtein = if (ingredients.any { it.protein != null }) {
            ingredients.sumOf { (it.protein ?: 0.0) * it.amount / 100.0 }
        } else {
            null
        }
        val totalCarbs = if (ingredients.any { it.carbs != null }) {
            ingredients.sumOf { (it.carbs ?: 0.0) * it.amount / 100.0 }
        } else {
            null
        }
        val totalFat = if (ingredients.any { it.fat != null }) {
            ingredients.sumOf { (it.fat ?: 0.0) * it.amount / 100.0 }
        } else {
            null
        }

        val perPortionKcal = totalKcal / totalPortions
        val perPortionProtein = totalProtein?.let { it / totalPortions }
        val perPortionCarbs = totalCarbs?.let { it / totalPortions }
        val perPortionFat = totalFat?.let { it / totalPortions }

        val entry = FoodEntry(
            date = date,
            name = recipe.name,
            kcal = (perPortionKcal * eatenPortions).toInt(),
            protein = perPortionProtein?.let { it * eatenPortions },
            carbs = perPortionCarbs?.let { it * eatenPortions },
            fat = perPortionFat?.let { it * eatenPortions },
            amount = eatenPortions,
            categoryId = categoryId,
            time = time
        )

        foodEntryDao.insert(entry)
    }

    suspend fun copyRecipe(recipeId: Long): Long {
        val original = recipeDao.getById(recipeId)
            ?: throw IllegalArgumentException("Recipe with id $recipeId not found")

        val newRecipe = original.copy(
            id = 0,
            name = "${original.name} (Kopie)",
            status = "in_progress"
        )
        val newRecipeId = recipeDao.insert(newRecipe)

        val ingredients = recipeIngredientDao.getByRecipeIdOnce(recipeId)
        for (ingredient in ingredients) {
            recipeIngredientDao.insert(
                ingredient.copy(
                    id = 0,
                    recipeId = newRecipeId
                )
            )
        }

        return newRecipeId
    }

    suspend fun upsertIngredient(
        name: String,
        kcalPer100: Double,
        referenceUnit: String,
        protein: Double?,
        carbs: Double?,
        fat: Double?
    ) {
        val existing = ingredientDao.getByName(name)
        if (existing != null) {
            ingredientDao.update(
                existing.copy(
                    kcalPer100 = kcalPer100,
                    referenceUnit = referenceUnit,
                    protein = protein,
                    carbs = carbs,
                    fat = fat
                )
            )
        } else {
            ingredientDao.insert(
                Ingredient(
                    name = name,
                    kcalPer100 = kcalPer100,
                    referenceUnit = referenceUnit,
                    protein = protein,
                    carbs = carbs,
                    fat = fat
                )
            )
        }
    }
}
