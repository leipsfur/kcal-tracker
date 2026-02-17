package de.leipsfur.kcal_track.data.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.leipsfur.kcal_track.data.db.KcalTrackDatabase
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class FoodEntryDaoTest {
    private lateinit var database: KcalTrackDatabase
    private lateinit var dao: FoodEntryDao
    private lateinit var categoryDao: FoodCategoryDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, KcalTrackDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.foodEntryDao()
        categoryDao = database.foodCategoryDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetFoodEntry() = runBlocking {
        // Insert category first to satisfy FK
        val category = FoodCategory(id = 1, name = "Test Category", sortOrder = 0)
        categoryDao.insert(category)

        val entry = FoodEntry(
            date = LocalDate.now(),
            name = "Test Food",
            kcal = 100,
            amount = 1.0,
            categoryId = 1,
            portionUnit = "g"
        )
        
        dao.insert(entry)
        
        val loaded = dao.getByDate(LocalDate.now()).first()
        assertEquals(1, loaded.size)
        assertEquals("Test Food", loaded[0].name)
        assertEquals(100, loaded[0].kcal)
    }

    @Test
    fun updateFoodEntry() = runBlocking {
        val category = FoodCategory(id = 1, name = "Test Category", sortOrder = 0)
        categoryDao.insert(category)

        val entry = FoodEntry(
            date = LocalDate.now(),
            name = "Test Food",
            kcal = 100,
            amount = 1.0,
            categoryId = 1
        )
        val id = dao.insert(entry)
        
        val updatedEntry = entry.copy(id = id, name = "Updated Food", kcal = 200)
        dao.update(updatedEntry)
        
        val loaded = dao.getById(id)
        assertEquals("Updated Food", loaded?.name)
        assertEquals(200, loaded?.kcal)
    }
}
