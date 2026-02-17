package de.leipsfur.kcal_track.data.repository

import de.leipsfur.kcal_track.data.db.dao.BmrPeriodDao
import de.leipsfur.kcal_track.data.db.entity.BmrPeriod
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
import java.time.LocalDate

class SettingsRepositoryTest {

    private val bmrPeriodDao = mockk<BmrPeriodDao>()
    private lateinit var repository: SettingsRepository

    @Before
    fun setup() {
        repository = SettingsRepository(bmrPeriodDao)
    }

    @Test
    fun getBmrForDate_returnsDaoFlow() = runTest {
        val date = LocalDate.of(2026, 2, 17)
        every { bmrPeriodDao.getBmrForDate(date) } returns flowOf(2100)

        assertEquals(2100, repository.getBmrForDate(date).first())
    }

    @Test
    fun updateBmr_upsertsPeriodForProvidedStartDate() = runTest {
        val startDate = LocalDate.of(2026, 2, 17)
        coEvery { bmrPeriodDao.upsert(any()) } returns Unit

        repository.updateBmr(bmr = 2200, startDate = startDate)

        coVerify(exactly = 1) {
            bmrPeriodDao.upsert(BmrPeriod(startDate = startDate, bmr = 2200))
        }
    }
}
