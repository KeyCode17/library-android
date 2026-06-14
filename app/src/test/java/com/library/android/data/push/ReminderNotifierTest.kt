package com.library.android.data.push

import android.app.NotificationManager
import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

/**
 * Incoming-push handling: [ReminderNotifier] posts a system notification. Robolectric's shadow
 * NotificationManager captures it — no Firebase/network involved.
 */
@RunWith(RobolectricTestRunner::class)
class ReminderNotifierTest {

    @Test
    fun show_postsASystemNotification() {
        val context = RuntimeEnvironment.getApplication() as Context
        val notifier = ReminderNotifier(context)

        notifier.show("Library reminder", "Pale Fire is due soon.")

        val manager = context.getSystemService(NotificationManager::class.java)
        assertEquals(1, shadowOf(manager).size())
    }
}
