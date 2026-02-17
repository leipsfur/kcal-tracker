package de.leipsfur.kcal_track.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import de.leipsfur.kcal_track.data.db.dao.ActivityCategoryDao
import de.leipsfur.kcal_track.data.db.dao.ActivityEntryDao
import de.leipsfur.kcal_track.data.db.dao.ActivityTemplateDao
import de.leipsfur.kcal_track.data.db.dao.FoodCategoryDao
import de.leipsfur.kcal_track.data.db.dao.FoodEntryDao
import de.leipsfur.kcal_track.data.db.dao.FoodTemplateDao
import de.leipsfur.kcal_track.data.db.dao.UserSettingsDao
import de.leipsfur.kcal_track.data.db.dao.WeightEntryDao
import de.leipsfur.kcal_track.data.db.entity.ActivityCategory
import de.leipsfur.kcal_track.data.db.entity.ActivityEntry
import de.leipsfur.kcal_track.data.db.entity.ActivityTemplate
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import de.leipsfur.kcal_track.data.db.entity.FoodTemplate
import de.leipsfur.kcal_track.data.db.entity.UserSettings
import de.leipsfur.kcal_track.data.db.entity.WeightEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FoodCategory::class,
        ActivityCategory::class,
        FoodTemplate::class,
        FoodEntry::class,
        ActivityTemplate::class,
        ActivityEntry::class,
        WeightEntry::class,
        UserSettings::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class KcalTrackDatabase : RoomDatabase() {
    abstract fun foodCategoryDao(): FoodCategoryDao
    abstract fun activityCategoryDao(): ActivityCategoryDao
    abstract fun foodTemplateDao(): FoodTemplateDao
    abstract fun foodEntryDao(): FoodEntryDao
    abstract fun activityTemplateDao(): ActivityTemplateDao
    abstract fun activityEntryDao(): ActivityEntryDao
    abstract fun weightEntryDao(): WeightEntryDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: KcalTrackDatabase? = null

        fun getInstance(context: Context): KcalTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): KcalTrackDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                KcalTrackDatabase::class.java,
                "kcal_track.db"
            )
                .addCallback(SeedDatabaseCallback())
                .build()
        }
    }

    private class SeedDatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedFoodCategories(database.foodCategoryDao())
                    seedActivityCategories(database.activityCategoryDao())
                }
            }
        }

        private suspend fun seedFoodCategories(dao: FoodCategoryDao) {
            val categories = listOf(
                FoodCategory(name = "Frühstück", sortOrder = 0),
                FoodCategory(name = "Mittagessen", sortOrder = 1),
                FoodCategory(name = "Abendessen", sortOrder = 2),
                FoodCategory(name = "Snack", sortOrder = 3),
                FoodCategory(name = "Alkohol", sortOrder = 4)
            )
            categories.forEach { dao.insert(it) }
        }

        private suspend fun seedActivityCategories(dao: ActivityCategoryDao) {
            val categories = listOf(
                ActivityCategory(name = "Cardio", sortOrder = 0),
                ActivityCategory(name = "Krafttraining", sortOrder = 1),
                ActivityCategory(name = "Alltag", sortOrder = 2)
            )
            categories.forEach { dao.insert(it) }
        }
    }
}
