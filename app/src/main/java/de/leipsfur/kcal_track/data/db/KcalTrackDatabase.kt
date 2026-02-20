package de.leipsfur.kcal_track.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.leipsfur.kcal_track.data.db.dao.ActivityCategoryDao
import de.leipsfur.kcal_track.data.db.dao.ActivityEntryDao
import de.leipsfur.kcal_track.data.db.dao.ActivityTemplateDao
import de.leipsfur.kcal_track.data.db.dao.BmrPeriodDao
import de.leipsfur.kcal_track.data.db.dao.FoodCategoryDao
import de.leipsfur.kcal_track.data.db.dao.FoodEntryDao
import de.leipsfur.kcal_track.data.db.dao.FoodTemplateDao
import de.leipsfur.kcal_track.data.db.dao.IngredientDao
import de.leipsfur.kcal_track.data.db.dao.RecipeDao
import de.leipsfur.kcal_track.data.db.dao.RecipeIngredientDao
import de.leipsfur.kcal_track.data.db.dao.UserSettingsDao
import de.leipsfur.kcal_track.data.db.dao.WeightEntryDao
import de.leipsfur.kcal_track.data.db.entity.ActivityCategory
import de.leipsfur.kcal_track.data.db.entity.ActivityEntry
import de.leipsfur.kcal_track.data.db.entity.ActivityTemplate
import de.leipsfur.kcal_track.data.db.entity.BmrPeriod
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.db.entity.FoodEntry
import de.leipsfur.kcal_track.data.db.entity.FoodTemplate
import de.leipsfur.kcal_track.data.db.entity.Ingredient
import de.leipsfur.kcal_track.data.db.entity.Recipe
import de.leipsfur.kcal_track.data.db.entity.RecipeIngredient
import de.leipsfur.kcal_track.data.db.entity.UserSettings
import de.leipsfur.kcal_track.data.db.entity.WeightEntry

@Database(
    entities = [
        FoodCategory::class,
        ActivityCategory::class,
        FoodTemplate::class,
        FoodEntry::class,
        ActivityTemplate::class,
        ActivityEntry::class,
        WeightEntry::class,
        UserSettings::class,
        BmrPeriod::class,
        Ingredient::class,
        Recipe::class,
        RecipeIngredient::class
    ],
    version = 5,
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
    abstract fun bmrPeriodDao(): BmrPeriodDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeDao(): RecipeDao
    abstract fun recipeIngredientDao(): RecipeIngredientDao

    companion object {
        @Volatile
        private var INSTANCE: KcalTrackDatabase? = null

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE food_entry ADD COLUMN portion_unit TEXT")
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `bmr_period` (
                        `start_date` INTEGER NOT NULL,
                        `bmr` INTEGER NOT NULL,
                        PRIMARY KEY(`start_date`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO bmr_period(start_date, bmr)
                    SELECT CAST(julianday('now', 'localtime') - 2440587.5 AS INTEGER), bmr
                    FROM user_settings
                    WHERE id = 1 AND bmr IS NOT NULL
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE food_entry ADD COLUMN time TEXT NOT NULL DEFAULT '12:00'"
                )
            }
        }

        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `ingredient` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `kcal_per_100` REAL NOT NULL,
                        `reference_unit` TEXT NOT NULL,
                        `protein` REAL,
                        `carbs` REAL,
                        `fat` REAL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `recipe` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `created_date` INTEGER NOT NULL,
                        `total_portions` REAL,
                        `status` TEXT NOT NULL DEFAULT 'in_progress'
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `recipe_ingredient` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `recipe_id` INTEGER NOT NULL,
                        `ingredient_id` INTEGER,
                        `name` TEXT NOT NULL,
                        `kcal_per_100` REAL NOT NULL,
                        `reference_unit` TEXT NOT NULL,
                        `amount` REAL NOT NULL,
                        `protein` REAL,
                        `carbs` REAL,
                        `fat` REAL,
                        FOREIGN KEY(`recipe_id`) REFERENCES `recipe`(`id`) ON DELETE CASCADE,
                        FOREIGN KEY(`ingredient_id`) REFERENCES `ingredient`(`id`) ON DELETE SET NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_recipe_ingredient_recipe_id` ON `recipe_ingredient` (`recipe_id`)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_recipe_ingredient_ingredient_id` ON `recipe_ingredient` (`ingredient_id`)"
                )

                // Seed ingredient database for existing users
                for (ingredient in IngredientSeedData.ingredients) {
                    val escapedName = ingredient.name.replace("'", "''")
                    db.execSQL(
                        """INSERT INTO ingredient (name, kcal_per_100, reference_unit, protein, carbs, fat)
                           VALUES ('$escapedName', ${ingredient.kcalPer100}, '${ingredient.referenceUnit}',
                                   ${ingredient.protein}, ${ingredient.carbs}, ${ingredient.fat})"""
                    )
                }
            }
        }

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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .addCallback(SeedDatabaseCallback())
                .build()
        }
    }

    private class SeedDatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Use direct SQL to avoid race condition with DAO access via INSTANCE
            db.execSQL("INSERT INTO food_category (name, sortOrder) VALUES ('Frühstück', 0)")
            db.execSQL("INSERT INTO food_category (name, sortOrder) VALUES ('Mittagessen', 1)")
            db.execSQL("INSERT INTO food_category (name, sortOrder) VALUES ('Abendessen', 2)")
            db.execSQL("INSERT INTO food_category (name, sortOrder) VALUES ('Snack', 3)")
            db.execSQL("INSERT INTO food_category (name, sortOrder) VALUES ('Alkohol', 4)")
            db.execSQL("INSERT INTO activity_category (name, sortOrder) VALUES ('Cardio', 0)")
            db.execSQL("INSERT INTO activity_category (name, sortOrder) VALUES ('Krafttraining', 1)")
            db.execSQL("INSERT INTO activity_category (name, sortOrder) VALUES ('Alltag', 2)")

            // Seed ingredient database
            for (ingredient in IngredientSeedData.ingredients) {
                val escapedName = ingredient.name.replace("'", "''")
                db.execSQL(
                    """INSERT INTO ingredient (name, kcal_per_100, reference_unit, protein, carbs, fat)
                       VALUES ('$escapedName', ${ingredient.kcalPer100}, '${ingredient.referenceUnit}',
                               ${ingredient.protein}, ${ingredient.carbs}, ${ingredient.fat})"""
                )
            }
        }
    }
}
