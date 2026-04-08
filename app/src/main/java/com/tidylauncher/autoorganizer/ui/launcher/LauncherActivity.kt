package com.tidylauncher.autoorganizer.ui.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.tidylauncher.autoorganizer.TidyLauncherApplication
import com.tidylauncher.autoorganizer.ui.common.ViewModelFactory
import com.tidylauncher.autoorganizer.ui.onboarding.OnboardingActivity
import com.tidylauncher.autoorganizer.ui.settings.SettingsActivity
import com.tidylauncher.autoorganizer.ui.theme.TidyLauncherTheme

class LauncherActivity : ComponentActivity() {
    private val viewModel: LauncherViewModel by viewModels {
        val container = (application as TidyLauncherApplication).appContainer
        ViewModelFactory {
            LauncherViewModel(
                appInventoryProvider = container.appInventoryProvider,
                settingsRepository = container.settingsRepository,
                autoArrangeCoordinator = container.autoArrangeCoordinator,
                billingManager = container.billingManager,
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TidyLauncherTheme {
                val state by viewModel.uiState.collectAsState()

                LaunchedEffect(state.needsOnboarding) {
                    if (state.needsOnboarding) {
                        startActivity(Intent(this@LauncherActivity, OnboardingActivity::class.java))
                        finish()
                    }
                }

                LauncherScreen(
                    state = state,
                    packageManager = packageManager,
                    onQueryChange = viewModel::updateQuery,
                    onDrawerTabChange = viewModel::updateDrawerTab,
                    onLaunchApp = { componentName ->
                        viewModel.launchIntentFor(componentName)?.let(::startActivity)
                    },
                    onOpenSettings = {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    },
                    onRefresh = viewModel::refresh,
                )
            }
        }
    }
}

