package de.leipsfur.kcal_track.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredient")
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "kcal_per_100") val kcalPer100: Double,
    @ColumnInfo(name = "reference_unit") val referenceUnit: String, // "g" or "ml"
    val protein: Double? = null,
    val carbs: Double? = null,
    val fat: Double? = null
)
