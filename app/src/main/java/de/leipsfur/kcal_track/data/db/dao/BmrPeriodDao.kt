package de.leipsfur.kcal_track.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.leipsfur.kcal_track.data.db.entity.BmrPeriod
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface BmrPeriodDao {
    @Query("SELECT * FROM bmr_period ORDER BY start_date ASC")
    fun getAll(): Flow<List<BmrPeriod>>

    @Query(
        """
        SELECT bmr FROM bmr_period
        WHERE start_date = COALESCE(
            (SELECT MAX(start_date) FROM bmr_period WHERE start_date <= :date),
            (SELECT MIN(start_date) FROM bmr_period)
        )
        LIMIT 1
        """
    )
    fun getBmrForDate(date: LocalDate): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(period: BmrPeriod)
}
