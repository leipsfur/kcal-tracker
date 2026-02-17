package de.leipsfur.kcal_track.data.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.leipsfur.kcal_track.data.db.KcalTrackDatabase
import de.leipsfur.kcal_track.data.db.entity.BmrPeriod
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class BmrPeriodDaoTest {
    private lateinit var database: KcalTrackDatabase
    private lateinit var dao: BmrPeriodDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, KcalTrackDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.bmrPeriodDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun getBmrForDate_withNoPeriods_returnsNull() = runBlocking {
        val bmr = dao.getBmrForDate(LocalDate.of(2026, 2, 17)).first()
        assertNull(bmr)
    }

    @Test
    fun getBmrForDate_beforeFirstPeriod_returnsEarliestPeriod() = runBlocking {
        dao.upsert(BmrPeriod(startDate = LocalDate.of(2026, 2, 17), bmr = 2100))

        val bmr = dao.getBmrForDate(LocalDate.of(2026, 2, 10)).first()

        assertEquals(2100, bmr)
    }

    @Test
    fun getBmrForDate_exactAndBetweenPeriods_resolvesExpectedPeriod() = runBlocking {
        dao.upsert(BmrPeriod(startDate = LocalDate.of(2026, 2, 1), bmr = 1900))
        dao.upsert(BmrPeriod(startDate = LocalDate.of(2026, 2, 15), bmr = 2100))
        dao.upsert(BmrPeriod(startDate = LocalDate.of(2026, 3, 1), bmr = 2000))

        val exactBmr = dao.getBmrForDate(LocalDate.of(2026, 2, 15)).first()
        val betweenBmr = dao.getBmrForDate(LocalDate.of(2026, 2, 20)).first()

        assertEquals(2100, exactBmr)
        assertEquals(2100, betweenBmr)
    }

    @Test
    fun upsert_sameStartDate_updatesExistingPeriod() = runBlocking {
        val date = LocalDate.of(2026, 2, 17)
        dao.upsert(BmrPeriod(startDate = date, bmr = 2000))
        dao.upsert(BmrPeriod(startDate = date, bmr = 2300))

        val periods = dao.getAll().first()
        val bmr = dao.getBmrForDate(date).first()

        assertEquals(1, periods.size)
        assertEquals(2300, bmr)
    }
}
