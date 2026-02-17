package de.leipsfur.kcal_track.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.leipsfur.kcal_track.data.db.entity.ActivityEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ActivityEntryDao {
    @Query("SELECT * FROM activity_entry WHERE date = :date ORDER BY id ASC")
    fun getByDate(date: LocalDate): Flow<List<ActivityEntry>>

    @Query("SELECT * FROM activity_entry WHERE id = :id")
    suspend fun getById(id: Long): ActivityEntry?

    @Query("SELECT COALESCE(SUM(kcal), 0) FROM activity_entry WHERE date = :date")
    fun getTotalKcalForDate(date: LocalDate): Flow<Int>

    @Insert
    suspend fun insert(entry: ActivityEntry): Long

    @Update
    suspend fun update(entry: ActivityEntry)

    @Delete
    suspend fun delete(entry: ActivityEntry)
}
