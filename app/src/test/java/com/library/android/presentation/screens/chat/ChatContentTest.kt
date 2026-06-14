package com.library.android.presentation.screens.chat

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.library.android.domain.model.ChatMessage
import com.library.android.presentation.theme.LibraryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Compose UI tests for the chat screen (JVM/Robolectric): history + live messages render with
 * the composer and room chips; the signed-out gate shows for anonymous users.
 */
@RunWith(RobolectricTestRunner::class)
class ChatContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val messages = listOf(
        ChatMessage("m1", "ask-a-librarian", "user-aaaa", "Is GEB available?", "2026-06-20T14:30:00Z"),
        ChatMessage("m2", "ask-a-librarian", "user-bbbb", "Yes — shelf R21.", "2026-06-20T14:31:00Z"),
    )

    @Test
    fun renders_historyAndComposer() {
        composeRule.setContent {
            LibraryTheme {
                ChatContent(
                    ChatUiState(
                        room = "ask-a-librarian",
                        rooms = listOf("ask-a-librarian", "fiction"),
                        connection = ConnectionState.Connected,
                        messages = messages,
                    ),
                )
            }
        }

        composeRule.onNodeWithText("Is GEB available?").assertIsDisplayed()
        composeRule.onNodeWithText("Yes — shelf R21.").assertIsDisplayed()
        composeRule.onNodeWithText("Connected").assertIsDisplayed()
        composeRule.onNodeWithText("Send").assertIsDisplayed()
        composeRule.onNodeWithTag(ChatTestTags.INPUT).assertIsDisplayed()
    }

    @Test
    fun anonymous_showsSignInGate() {
        composeRule.setContent {
            LibraryTheme { ChatContent(ChatUiState(isAnonymous = true)) }
        }

        composeRule.onNodeWithText("Sign in to join the chat").assertIsDisplayed()
        composeRule.onNodeWithText("Log in").assertIsDisplayed()
    }
}
