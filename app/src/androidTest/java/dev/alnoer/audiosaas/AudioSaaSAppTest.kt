package dev.alnoer.audiosaas

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.alnoer.audiosaas.core.designsystem.theme.AudioSaaSTheme
import org.junit.Rule
import org.junit.Test

class AudioSaaSAppTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun skippingOnboardingOpensSignIn() {
        composeRule.setContent {
            AudioSaaSTheme {
                AudioSaaSApp()
            }
        }

        composeRule.onNodeWithText("Пропустить").performClick()

        composeRule.onNodeWithText("Вход").assertIsDisplayed()
    }
}

