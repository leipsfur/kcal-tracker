package de.leipsfur.kcal_track.data.repository

import de.leipsfur.kcal_track.data.db.dao.UserSettingsDao
import de.leipsfur.kcal_track.data.db.entity.UserSettings
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val userSettingsDao: UserSettingsDao
) {
    fun getSettings(): Flow<UserSettings?> = userSettingsDao.get()

    suspend fun updateBmr(bmr: Int) {
        userSettingsDao.insertOrUpdate(UserSettings(id = 1, bmr = bmr))
    }
}
