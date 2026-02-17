package de.leipsfur.kcal_track.data.db

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KcalTrackDatabaseMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        KcalTrackDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate2To3_movesGlobalBmrToPeriodTable() {
        val dbName = "kcal-track-migration-2-3"

        helper.createDatabase(dbName, 2).apply {
            execSQL("INSERT INTO user_settings(id, bmr) VALUES(1, 2150)")
            close()
        }

        helper.runMigrationsAndValidate(
            dbName,
            3,
            true,
            KcalTrackDatabase.MIGRATION_2_3
        ).apply {
            query("SELECT start_date, bmr FROM bmr_period").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(2150, cursor.getInt(1))
                assertFalse(cursor.moveToNext())
            }
            close()
        }
    }

    @Test
    fun migrate2To3_withNullBmr_keepsPeriodTableEmpty() {
        val dbName = "kcal-track-migration-2-3-null"

        helper.createDatabase(dbName, 2).apply {
            execSQL("INSERT INTO user_settings(id, bmr) VALUES(1, NULL)")
            close()
        }

        helper.runMigrationsAndValidate(
            dbName,
            3,
            true,
            KcalTrackDatabase.MIGRATION_2_3
        ).apply {
            query("SELECT COUNT(*) FROM bmr_period").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(0, cursor.getInt(0))
            }
            close()
        }
    }
}
