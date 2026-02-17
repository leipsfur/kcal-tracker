package de.leipsfur.kcal_track.data.repository

import de.leipsfur.kcal_track.data.db.dao.UserSettingsDao
import de.leipsfur.kcal_track.data.db.entity.UserSettings
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsRepositoryTest {

    private val userSettingsDao = mockk<UserSettingsDao>()
    private lateinit var repository: SettingsRepository

    @Before
    fun setup() {
        repository = SettingsRepository(userSettingsDao)
    }

    @Test
    fun getSettings_returnsDaoFlow() = runTest {
        val settings = UserSettings(id = 1, bmr = 2100)
        every { userSettingsDao.get() } returns flowOf(settings)

        assertEquals(settings, repository.getSettings().first())
    }

    @Test
    fun updateBmr_writesSingletonSettingsRow() = runTest {
        coEvery { userSettingsDao.insertOrUpdate(any()) } returns Unit

        repository.updateBmr(2200)

        coVerify(exactly = 1) {
            userSettingsDao.insertOrUpdate(UserSettings(id = 1, bmr = 2200))
        }
    }
}
