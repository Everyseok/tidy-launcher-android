package com.tidylauncher.autoorganizer.ui.onboarding

import android.app.role.RoleManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tidylauncher.autoorganizer.R
import com.tidylauncher.autoorganizer.TidyLauncherApplication
import com.tidylauncher.autoorganizer.domain.model.PageMode
import com.tidylauncher.autoorganizer.domain.model.RecommendationReason
import com.tidylauncher.autoorganizer.domain.model.SortMode
import com.tidylauncher.autoorganizer.ui.common.ViewModelFactory
import com.tidylauncher.autoorganizer.ui.launcher.LauncherActivity
import com.tidylauncher.autoorganizer.ui.theme.TidyLauncherTheme

class OnboardingActivity : ComponentActivity() {
    private val viewModel: OnboardingViewModel by viewModels {
        val container = (application as TidyLauncherApplication).appContainer
        ViewModelFactory {
            OnboardingViewModel(
                appInventoryProvider = container.appInventoryProvider,
                recommendationEngine = container.recommendationEngine,
                settingsRepository = container.settingsRepository,
                autoArrangeCoordinator = container.autoArrangeCoordinator,
            )
        }
    }

    private val homeRoleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        startLauncher()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TidyLauncherTheme {
                val state by viewModel.uiState.collectAsState()
                OnboardingScreen(
                    state = state,
                    onSelectSortMode = viewModel::selectSortMode,
                    onSelectPageMode = viewModel::selectPageMode,
                    onConfirm = {
                        viewModel.completeOnboarding {
                            requestLauncherRoleOrContinue()
                        }
                    },
                )
            }
        }
    }

    private fun requestLauncherRoleOrContinue() {
        val roleManager = getSystemService(RoleManager::class.java)
        if (roleManager != null &&
            roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
            !roleManager.isRoleHeld(RoleManager.ROLE_HOME)
        ) {
            homeRoleLauncher.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME))
        } else {
            startLauncher()
        }
    }

    private fun startLauncher() {
        startActivity(Intent(this, LauncherActivity::class.java))
        finish()
    }
}

@Composable
private fun OnboardingScreen(
    state: OnboardingUiState,
    onSelectSortMode: (SortMode) -> Unit,
    onSelectPageMode: (PageMode) -> Unit,
    onConfirm: () -> Unit,
) {
    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(20.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Text(
                    text = stringResource(R.string.onboarding_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.onboarding_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Card {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.recommended_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(text = stringResource(R.string.apps_detected, state.appCount))
                        Text(text = recommendationText(state.recommendation?.sortReason))
                        Text(text = recommendationText(state.recommendation?.pageReason))
                    }
                }
                SelectionGroup(
                    title = stringResource(R.string.label_sort_mode),
                    options = listOf(SortMode.FUNCTION, SortMode.COLOR),
                    selected = state.selectedSortMode,
                    label = { mode ->
                        stringResource(
                            if (mode == SortMode.FUNCTION) {
                                R.string.sort_mode_function
                            } else {
                                R.string.sort_mode_color
                            },
                        )
                    },
                    onSelect = onSelectSortMode,
                )
                SelectionGroup(
                    title = stringResource(R.string.label_page_mode),
                    options = listOf(PageMode.ONE_PAGE, PageMode.TWO_PAGES),
                    selected = state.selectedPageMode,
                    label = { page ->
                        stringResource(
                            if (page == PageMode.ONE_PAGE) {
                                R.string.page_mode_one
                            } else {
                                R.string.page_mode_two
                            },
                        )
                    },
                    onSelect = onSelectPageMode,
                )
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.loading,
                ) {
                    Text(text = stringResource(R.string.onboarding_confirm))
                }
            }
        }
    }
}

@Composable
private fun recommendationText(reason: RecommendationReason?): String = when (reason) {
    RecommendationReason.FUNCTION_SPREAD -> stringResource(R.string.recommendation_reason_function)
    RecommendationReason.COLOR_BALANCE -> stringResource(R.string.recommendation_reason_color)
    RecommendationReason.ONE_PAGE_DENSITY -> stringResource(R.string.recommendation_pages_one)
    RecommendationReason.TWO_PAGE_BREATHING_ROOM -> stringResource(R.string.recommendation_pages_two)
    null -> ""
}

@Composable
private fun <T> SelectionGroup(
    title: String,
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp),
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            options.forEach { option ->
                FilterChip(
                    selected = selected == option,
                    onClick = { onSelect(option) },
                    label = { Text(label(option)) },
                )
            }
        }
    }
}
