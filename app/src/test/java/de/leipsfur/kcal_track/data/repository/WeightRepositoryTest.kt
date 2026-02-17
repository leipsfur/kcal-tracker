package de.leipsfur.kcal_track.data.repository

import de.leipsfur.kcal_track.data.db.dao.WeightEntryDao
import de.leipsfur.kcal_track.data.db.entity.WeightEntry
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

class WeightRepositoryTest {

    private val weightEntryDao = mockk<WeightEntryDao>()
    private lateinit var repository: WeightRepository

    @Before
    fun setup() {
        repository = WeightRepository(weightEntryDao)
    }

    @Test
    fun methods_delegateToWeightDao() = runTest {
        val date = LocalDate.of(2026, 2, 17)
        val entry = WeightEntry(id = 1, date = date, weightKg = 80.5)
        val entries = listOf(entry)

        every { weightEntryDao.getAll() } returns flowOf(entries)
        coEvery { weightEntryDao.getByDate(date) } returns entry
        coEvery { weightEntryDao.insert(entry) } returns 1L
        coEvery { weightEntryDao.delete(entry) } returns Unit

        assertEquals(entries, repository.getAll().first())
        assertEquals(entry, repository.getByDate(date))
        assertEquals(1L, repository.insert(entry))
        repository.delete(entry)

        coVerify(exactly = 1) { weightEntryDao.getByDate(date) }
        coVerify(exactly = 1) { weightEntryDao.insert(entry) }
        coVerify(exactly = 1) { weightEntryDao.delete(entry) }
    }
}
