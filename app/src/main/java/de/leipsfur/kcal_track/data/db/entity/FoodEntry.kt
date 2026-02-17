package de.leipsfur.kcal_track.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "food_entry",
    foreignKeys = [
        ForeignKey(
            entity = FoodTemplate::class,
            parentColumns = ["id"],
            childColumns = ["template_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = FoodCategory::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("date"), Index("template_id"), Index("category_id")]
)
data class FoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDate,
    @ColumnInfo(name = "template_id") val templateId: Long? = null,
    val name: String,
    val kcal: Int,
    val protein: Double? = null,
    val carbs: Double? = null,
    val fat: Double? = null,
    val amount: Double,
    @ColumnInfo(name = "category_id") val categoryId: Long
)
