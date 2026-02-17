package de.leipsfur.kcal_track.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Long = 1,
    val bmr: Int? = null
)
