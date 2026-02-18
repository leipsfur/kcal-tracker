package de.leipsfur.kcal_track.ui.navigation

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.leipsfur.kcal_track.MainActivity
import de.leipsfur.kcal_track.ui.UiTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SwipeNavigationUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun swipeLeftFromDashboard_opensFoodScreen() {
        composeRule.onNodeWithText("Dashboard").assertExists()

        composeRule.onNodeWithTag(UiTestTags.MAIN_TAB_SWIPE_CONTAINER).performTouchInput {
            swipeLeft()
        }

        composeRule.onNodeWithText("Essen").assertExists()
    }

    @Test
    fun swipeRightFromDashboard_wrapsToSettingsScreen() {
        composeRule.onNodeWithText("TDEE").assertExists()

        composeRule.onNodeWithTag(UiTestTags.MAIN_TAB_SWIPE_CONTAINER).performTouchInput {
            swipeRight()
        }

        composeRule.onNodeWithText("Grundumsatz").assertExists()
    }

    @Test
    fun swipeLeftFromSettings_wrapsToDashboardScreen() {
        composeRule.onNodeWithTag(UiTestTags.MAIN_TAB_SWIPE_CONTAINER).performTouchInput {
            swipeRight()
        }
        composeRule.onNodeWithText("Grundumsatz").assertExists()

        composeRule.onNodeWithTag(UiTestTags.MAIN_TAB_SWIPE_CONTAINER).performTouchInput {
            swipeLeft()
        }

        composeRule.onNodeWithText("TDEE").assertExists()
    }
}
