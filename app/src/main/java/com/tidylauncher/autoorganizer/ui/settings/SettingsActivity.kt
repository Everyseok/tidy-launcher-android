package com.tidylauncher.autoorganizer.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tidylauncher.autoorganizer.R
import com.tidylauncher.autoorganizer.TidyLauncherApplication
import com.tidylauncher.autoorganizer.domain.model.PageMode
import com.tidylauncher.autoorganizer.domain.model.PremiumEntitlement
import com.tidylauncher.autoorganizer.domain.model.SortMode
import com.tidylauncher.autoorganizer.domain.model.UserSettings
import com.tidylauncher.autoorganizer.ui.common.ViewModelFactory
import com.tidylauncher.autoorganizer.ui.theme.TidyLauncherTheme

class SettingsActivity : ComponentActivity() {
    private val viewModel: SettingsViewModel by viewModels {
        val container = (application as TidyLauncherApplication).appContainer
        ViewModelFactory {
            SettingsViewModel(
                settingsRepository = container.settingsRepository,
                billingManager = container.billingManager,
                autoArrangeCoordinator = container.autoArrangeCoordinator,
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TidyLauncherTheme {
                val settings by viewModel.settings.collectAsState()
                SettingsScreen(
                    settings = settings,
                    onSortModeChange = viewModel::updateSortMode,
                    onPageModeChange = viewModel::updatePageMode,
                    onRerun = viewModel::rerun,
                    onUnlockPremium = viewModel::unlockPremium,
                    onLockPremium = viewModel::lockPremium,
                )
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    settings: UserSettings,
    onSortModeChange: (SortMode) -> Unit,
    onPageModeChange: (PageMode) -> Unit,
    onRerun: () -> Unit,
    onUnlockPremium: () -> Unit,
    onLockPremium: () -> Unit,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(text = stringResource(R.string.label_sort_mode))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilterChip(
                            selected = settings.sortMode == SortMode.FUNCTION,
                            onClick = { onSortModeChange(SortMode.FUNCTION) },
                            label = { Text(stringResource(R.string.sort_mode_function)) },
                        )
                        FilterChip(
                            selected = settings.sortMode == SortMode.COLOR,
                            onClick = { onSortModeChange(SortMode.COLOR) },
                            label = { Text(stringResource(R.string.sort_mode_color)) },
                        )
                    }
                    Text(text = stringResource(R.string.label_page_mode))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilterChip(
                            selected = settings.pageMode == PageMode.ONE_PAGE,
                            onClick = { onPageModeChange(PageMode.ONE_PAGE) },
                            label = { Text(stringResource(R.string.page_mode_one)) },
                        )
                        FilterChip(
                            selected = settings.pageMode == PageMode.TWO_PAGES,
                            onClick = { onPageModeChange(PageMode.TWO_PAGES) },
                            label = { Text(stringResource(R.string.page_mode_two)) },
                        )
                    }
                    Button(onClick = onRerun, modifier = Modifier.fillMaxWidth()) {
                        Text(text = stringResource(R.string.settings_rerun))
                    }
                }
            }
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(text = stringResource(R.string.settings_privacy_title), style = MaterialTheme.typography.titleMedium)
                    Text(text = stringResource(R.string.settings_privacy_body))
                }
            }
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(text = stringResource(R.string.settings_premium_title), style = MaterialTheme.typography.titleMedium)
                    Text(text = stringResource(R.string.settings_premium_body))
                    Text(
                        text = if (settings.premiumEntitlement == PremiumEntitlement.UNLOCKED) {
                            stringResource(R.string.premium_unlocked)
                        } else {
                            stringResource(R.string.premium_locked)
                        },
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = onUnlockPremium) {
                            Text(text = stringResource(R.string.action_unlock))
                        }
                        Button(onClick = onLockPremium) {
                            Text(text = stringResource(R.string.action_reset))
                        }
                    }
                }
            }
        }
    }
}
