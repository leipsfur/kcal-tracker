package de.leipsfur.kcal_track

import android.app.Application
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import de.leipsfur.kcal_track.data.BackupManager
import de.leipsfur.kcal_track.data.db.KcalTrackDatabase
import de.leipsfur.kcal_track.data.repository.ActivityRepository
import de.leipsfur.kcal_track.data.repository.FoodRepository
import de.leipsfur.kcal_track.data.repository.SettingsRepository
import de.leipsfur.kcal_track.data.repository.RecipeRepository
import de.leipsfur.kcal_track.data.repository.WeightRepository
import de.leipsfur.kcal_track.ui.settings.NutritionLabelScanner

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

    val recipeRepository: RecipeRepository by lazy {
        RecipeRepository(
            database.recipeDao(),
            database.recipeIngredientDao(),
            database.ingredientDao(),
            database.foodEntryDao()
        )
    }

    val backupManager: BackupManager by lazy { BackupManager(this) }

    val nutritionLabelScanner: NutritionLabelScanner by lazy {
        NutritionLabelScanner(
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS),
            Generation.getClient()
        )
    }
}
