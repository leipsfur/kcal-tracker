package de.leipsfur.kcal_track.ui.critical

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.leipsfur.kcal_track.data.db.KcalTrackDatabase
import de.leipsfur.kcal_track.data.db.entity.ActivityCategory
import de.leipsfur.kcal_track.data.db.entity.ActivityEntry
import de.leipsfur.kcal_track.data.db.entity.FoodCategory
import de.leipsfur.kcal_track.data.repository.ActivityRepository
import de.leipsfur.kcal_track.data.repository.FoodRepository
import de.leipsfur.kcal_track.ui.UiTestTags
import de.leipsfur.kcal_track.ui.activity.ActivityScreen
import de.leipsfur.kcal_track.ui.activity.ActivityViewModel
import de.leipsfur.kcal_track.ui.food.FoodScreen
import de.leipsfur.kcal_track.ui.food.FoodViewModel
import de.leipsfur.kcal_track.ui.theme.KcaltrackTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class CriticalFlowsUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var database: KcalTrackDatabase
    private lateinit var foodRepository: FoodRepository
    private lateinit var activityRepository: ActivityRepository
    private val dateFlow = MutableStateFlow(LocalDate.of(2026, 2, 17))

    private var activityCategoryId: Long = 0

    @Before
    fun setup() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, KcalTrackDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        foodRepository = FoodRepository(
            database.foodTemplateDao(),
            database.foodEntryDao(),
            database.foodCategoryDao()
        )
        activityRepository = ActivityRepository(
            database.activityTemplateDao(),
            database.activityEntryDao(),
            database.activityCategoryDao()
        )

        database.foodCategoryDao().insert(FoodCategory(name = "Test Food", sortOrder = 0))
        activityCategoryId = database.activityCategoryDao()
            .insert(ActivityCategory(name = "Test Activity", sortOrder = 0))
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun foodManualEntry_happyPath_addsEntryToList() {
        setFoodContent()

        composeRule.onNodeWithTag(UiTestTags.FOOD_MANUAL_ENTRY_FAB).performClick()
        composeRule.onNodeWithTag(UiTestTags.FOOD_ENTRY_NAME_INPUT).performTextInput("Banane")
        composeRule.onNodeWithTag(UiTestTags.FOOD_ENTRY_KCAL_INPUT).performTextInput("100")
        composeRule.onNodeWithTag(UiTestTags.FOOD_ENTRY_SAVE_BUTTON).performClick()

        composeRule.onNodeWithText("Banane").assertExists()
    }

    @Test
    fun foodManualEntry_validation_showsNameErrorOnEmptySave() {
        setFoodContent()

        composeRule.onNodeWithTag(UiTestTags.FOOD_MANUAL_ENTRY_FAB).performClick()
        composeRule.onNodeWithTag(UiTestTags.FOOD_ENTRY_SAVE_BUTTON).performClick()

        composeRule.onNodeWithText("Name darf nicht leer sein").assertExists()
    }

    @Test
    fun activityManualEntry_happyPath_addsEntryToList() {
        setActivityContent()

        composeRule.onNodeWithTag(UiTestTags.ACTIVITY_MANUAL_ENTRY_FAB).performClick()
        composeRule.onNodeWithTag(UiTestTags.ACTIVITY_ENTRY_NAME_INPUT).performTextInput("Laufen")
        composeRule.onNodeWithTag(UiTestTags.ACTIVITY_ENTRY_KCAL_INPUT).performTextInput("300")
        composeRule.onNodeWithTag(UiTestTags.ACTIVITY_ENTRY_SAVE_BUTTON).performClick()

        composeRule.onNodeWithText("Laufen").assertExists()
    }

    @Test
    fun activityManualEntry_validation_showsKcalError() {
        setActivityContent()

        composeRule.onNodeWithTag(UiTestTags.ACTIVITY_MANUAL_ENTRY_FAB).performClick()
        composeRule.onNodeWithTag(UiTestTags.ACTIVITY_ENTRY_NAME_INPUT).performTextInput("Laufen")
        composeRule.onNodeWithTag(UiTestTags.ACTIVITY_ENTRY_SAVE_BUTTON).performClick()

        composeRule.onNodeWithText("kcal muss größer als 0 sein").assertExists()
    }

    @Test
    fun activityEditEntry_happyPath_updatesExistingEntry() {
        runBlocking {
            database.activityEntryDao().insert(
                ActivityEntry(
                    date = dateFlow.value,
                    name = "Laufen",
                    kcal = 100,
                    categoryId = activityCategoryId
                )
            )
        }

        setActivityContent()

        composeRule.onAllNodesWithTag(UiTestTags.ACTIVITY_ENTRY_EDIT_BUTTON).assertCountEquals(1)
        composeRule.onAllNodesWithTag(UiTestTags.ACTIVITY_ENTRY_EDIT_BUTTON)[0].performClick()
        composeRule.onNodeWithTag(UiTestTags.ACTIVITY_ENTRY_KCAL_INPUT).performTextClearance()
        composeRule.onNodeWithTag(UiTestTags.ACTIVITY_ENTRY_KCAL_INPUT).performTextInput("250")
        composeRule.onNodeWithTag(UiTestTags.ACTIVITY_ENTRY_SAVE_BUTTON).performClick()

        composeRule.onNodeWithText("250 kcal").assertExists()
    }

    @Test
    fun activityEditEntry_validation_showsNameError() {
        runBlocking {
            database.activityEntryDao().insert(
                ActivityEntry(
                    date = dateFlow.value,
                    name = "Radfahren",
                    kcal = 200,
                    categoryId = activityCategoryId
                )
            )
        }

        setActivityContent()

        composeRule.onAllNodesWithTag(UiTestTags.ACTIVITY_ENTRY_EDIT_BUTTON).assertCountEquals(1)
        composeRule.onAllNodesWithTag(UiTestTags.ACTIVITY_ENTRY_EDIT_BUTTON)[0].performClick()
        composeRule.onNodeWithTag(UiTestTags.ACTIVITY_ENTRY_NAME_INPUT).performTextClearance()
        composeRule.onNodeWithTag(UiTestTags.ACTIVITY_ENTRY_SAVE_BUTTON).performClick()

        composeRule.onNodeWithText("Name darf nicht leer sein").assertExists()
    }

    private fun setFoodContent() {
        val viewModel = FoodViewModel(
            foodRepository = foodRepository,
            dateFlow = dateFlow,
            onDateChangedCallback = { dateFlow.value = it },
            onDataChanged = {}
        )
        composeRule.setContent {
            KcaltrackTheme {
                FoodScreen(viewModel = viewModel)
            }
        }
        composeRule.waitUntil(timeoutMillis = 3_000) {
            viewModel.uiState.value.categories.isNotEmpty()
        }
    }

    private fun setActivityContent() {
        val viewModel = ActivityViewModel(
            activityRepository = activityRepository,
            dateFlow = dateFlow,
            onDateChangedCallback = { dateFlow.value = it },
            onDataChanged = {}
        )
        composeRule.setContent {
            KcaltrackTheme {
                ActivityScreen(viewModel = viewModel)
            }
        }
        composeRule.waitUntil(timeoutMillis = 3_000) {
            viewModel.uiState.value.categories.isNotEmpty()
        }
    }
}
