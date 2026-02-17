package de.leipsfur.kcal_track.data.repository

import de.leipsfur.kcal_track.data.db.dao.BmrPeriodDao
import de.leipsfur.kcal_track.data.db.entity.BmrPeriod
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class SettingsRepository(
    private val bmrPeriodDao: BmrPeriodDao
) {
    fun getAllBmrPeriods(): Flow<List<BmrPeriod>> = bmrPeriodDao.getAll()

    fun getBmrForDate(date: LocalDate): Flow<Int?> = bmrPeriodDao.getBmrForDate(date)

    suspend fun updateBmr(bmr: Int, startDate: LocalDate = LocalDate.now()) {
        bmrPeriodDao.upsert(BmrPeriod(startDate = startDate, bmr = bmr))
    }
}
