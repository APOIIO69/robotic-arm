package com.apollo.roboarm.ui.screens.details

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import com.apollo.roboarm.ui.theme.RoboArmTheme
import org.junit.Rule
import org.junit.Test

class RobotDetailsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun robotDetailsScreen_showsTabs() {
        composeTestRule.setContent {
            RoboArmTheme {
                RobotDetailsScreen(
                    robotId = 1,
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Телеметрия").assertIsDisplayed()
        composeTestRule.onNodeWithText("Управление").assertIsDisplayed()
    }
}
