package com.library.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. [HiltAndroidApp] generates the Hilt component graph so feature
 * modules added from M1 onward can inject their dependencies.
 */
@HiltAndroidApp
class LibraryApplication : Application()
