package com.library.android.presentation.screens.catalog

import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit test for the account-initials derivation behind the auth-aware catalog avatar. */
class CatalogAvatarTest {

    @Test
    fun `takes up to two letters from the email local part, uppercased`() {
        assertEquals("DA", avatarInitials("dana@stacks.app"))
        assertEquals("LI", avatarInitials("li@x.io"))
        assertEquals("A", avatarInitials("a@x.io"))
    }

    @Test
    fun `falls back when the local part has no letters or digits`() {
        assertEquals("?", avatarInitials("@nodomain"))
    }
}
