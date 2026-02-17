package de.leipsfur.kcal_track.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "bmr_period")
data class BmrPeriod(
    @PrimaryKey
    @ColumnInfo(name = "start_date")
    val startDate: LocalDate,
    val bmr: Int
)
