package de.leipsfur.kcal_track.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_template",
    foreignKeys = [
        ForeignKey(
            entity = FoodCategory::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("category_id")]
)
data class FoodTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val kcal: Int,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    val protein: Double? = null,
    val carbs: Double? = null,
    val fat: Double? = null,
    val portionSize: Double,
    val portionUnit: String
)
