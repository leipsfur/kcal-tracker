package de.leipsfur.kcal_track.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_category")
data class ActivityCategory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sortOrder: Int
)
