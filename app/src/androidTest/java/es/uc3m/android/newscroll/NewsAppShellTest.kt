package es.uc3m.android.newscroll

import android.Manifest
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test


class NewsAppShellTest {
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS
    )

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appShell_displaysTopBarTitle() {
        // Assert the title is Newscroll
        composeTestRule
            .onAllNodesWithText("Newscroll")
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun appShell_opensDrawer_showsDefaultHeaderText() {
        // Click burger menu
        composeTestRule
            .onNodeWithContentDescription("Open Menu")
            .performClick()

        // Assert the title is Newscroll
        composeTestRule
            .onAllNodesWithText("Newscroll")
            .onFirst()
            .assertIsDisplayed()

        // Asser without signing in messages is not shown
        composeTestRule
            .onNodeWithText("Messages")
            .assertDoesNotExist()

        // Asser without signing saved articles is not shown
        composeTestRule
            .onNodeWithText("Saved Articles")
            .assertDoesNotExist()
    }

    @Test
    fun appShell_login_updatesDrawerWithUsernameAndProtectedOptions() {
        composeTestRule
            .onNodeWithContentDescription("Open Menu")
            .performClick()

        composeTestRule
            .onNodeWithText("Log in")
            .performClick()

        composeTestRule
            .onNodeWithText("Email")
            .performTextInput("sergio@gmail.com")

        composeTestRule
            .onNodeWithText("Password")
            .performTextInput("sergio")

        composeTestRule
            .onAllNodesWithText("Log in")
            .onFirst()
            .performClick()

        Thread.sleep(5000)

        composeTestRule
            .onNodeWithContentDescription("Open Menu")
            .performClick()

        // Assert title is user name
        composeTestRule
            .onAllNodesWithText("sergio")
            .onFirst()
            .assertIsDisplayed()

        // Assert messages is now visible
        composeTestRule
            .onNodeWithText("Messages")
            .assertIsDisplayed()

        // Assert saved articles is now visible
        composeTestRule
            .onNodeWithText("Saved Articles")
            .assertIsDisplayed()
    }

    @Test
    fun appShell_navigatesToSettings_andExpandsThemeMenu() {
        composeTestRule
            .onNodeWithContentDescription("Open Menu")
            .performClick()

        composeTestRule
            .onNodeWithText("User Preferences")
            .performClick()

        composeTestRule
            .onNodeWithText("App Theme")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("App Theme")
            .performClick()

        composeTestRule
            .onNodeWithText("Dark")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Dark")
            .performClick()
    }
}