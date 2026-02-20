package de.leipsfur.kcal_track.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe_ingredient",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipe_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["id"],
            childColumns = ["ingredient_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("recipe_id"), Index("ingredient_id")]
)
data class RecipeIngredient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "recipe_id") val recipeId: Long,
    @ColumnInfo(name = "ingredient_id") val ingredientId: Long? = null,
    val name: String,
    @ColumnInfo(name = "kcal_per_100") val kcalPer100: Double,
    @ColumnInfo(name = "reference_unit") val referenceUnit: String,
    val amount: Double, // amount used in the recipe in g or ml
    val protein: Double? = null,
    val carbs: Double? = null,
    val fat: Double? = null
)
