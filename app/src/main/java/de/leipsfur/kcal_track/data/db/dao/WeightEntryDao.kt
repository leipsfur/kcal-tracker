package de.leipsfur.kcal_track.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.leipsfur.kcal_track.data.db.entity.WeightEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WeightEntryDao {
    @Query("SELECT * FROM weight_entry ORDER BY date DESC")
    fun getAll(): Flow<List<WeightEntry>>

    @Query("SELECT * FROM weight_entry WHERE date = :date")
    suspend fun getByDate(date: LocalDate): WeightEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WeightEntry): Long

    @Delete
    suspend fun delete(entry: WeightEntry)
}
