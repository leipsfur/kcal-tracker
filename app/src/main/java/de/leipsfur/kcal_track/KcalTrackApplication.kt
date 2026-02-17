package de.leipsfur.kcal_track

import android.app.Application
import de.leipsfur.kcal_track.data.db.KcalTrackDatabase
import de.leipsfur.kcal_track.data.repository.ActivityRepository
import de.leipsfur.kcal_track.data.repository.FoodRepository
import de.leipsfur.kcal_track.data.repository.SettingsRepository
import de.leipsfur.kcal_track.data.repository.WeightRepository

class KcalTrackApplication : Application() {
    val database: KcalTrackDatabase by lazy { KcalTrackDatabase.getInstance(this) }

    val foodRepository: FoodRepository by lazy {
        FoodRepository(
            database.foodTemplateDao(),
            database.foodEntryDao(),
            database.foodCategoryDao()
        )
    }

    val activityRepository: ActivityRepository by lazy {
        ActivityRepository(
            database.activityTemplateDao(),
            database.activityEntryDao(),
            database.activityCategoryDao()
        )
    }

    val weightRepository: WeightRepository by lazy {
        WeightRepository(database.weightEntryDao())
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(database.bmrPeriodDao())
    }
}
