package de.leipsfur.kcal_track.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "recipe")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "created_date") val createdDate: LocalDate,
    @ColumnInfo(name = "total_portions") val totalPortions: Double? = null,
    val status: String = "in_progress" // "in_progress" or "completed"
)
