package de.leipsfur.kcal_track.data.repository

import de.leipsfur.kcal_track.data.db.dao.WeightEntryDao
import de.leipsfur.kcal_track.data.db.entity.WeightEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class WeightRepository(
    private val weightEntryDao: WeightEntryDao
) {
    fun getAll(): Flow<List<WeightEntry>> = weightEntryDao.getAll()

    suspend fun getByDate(date: LocalDate): WeightEntry? = weightEntryDao.getByDate(date)

    suspend fun insert(entry: WeightEntry): Long = weightEntryDao.insert(entry)

    suspend fun delete(entry: WeightEntry) = weightEntryDao.delete(entry)
}
