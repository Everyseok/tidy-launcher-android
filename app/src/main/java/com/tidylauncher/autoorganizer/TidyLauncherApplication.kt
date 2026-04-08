package com.tidylauncher.autoorganizer

import android.app.Application
import androidx.work.Configuration
import com.tidylauncher.autoorganizer.di.AppContainer

class TidyLauncherApplication : Application(), Configuration.Provider {
    val appContainer: AppContainer by lazy { AppContainer(this) }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}

