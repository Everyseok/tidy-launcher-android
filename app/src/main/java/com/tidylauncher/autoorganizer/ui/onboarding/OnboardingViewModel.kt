package com.tidylauncher.autoorganizer.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tidylauncher.autoorganizer.data.AppInventoryProvider
import com.tidylauncher.autoorganizer.data.local.SettingsRepository
import com.tidylauncher.autoorganizer.domain.RecommendationEngine
import com.tidylauncher.autoorganizer.domain.layout.AutoArrangeCoordinator
import com.tidylauncher.autoorganizer.domain.model.PageMode
import com.tidylauncher.autoorganizer.domain.model.RecommendedPlan
import com.tidylauncher.autoorganizer.domain.model.SortMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val loading: Boolean = true,
    val appCount: Int = 0,
    val recommendation: RecommendedPlan? = null,
    val selectedSortMode: SortMode = SortMode.FUNCTION,
    val selectedPageMode: PageMode = PageMode.ONE_PAGE,
)

class OnboardingViewModel(
    private val appInventoryProvider: AppInventoryProvider,
    private val recommendationEngine: RecommendationEngine,
    private val settingsRepository: SettingsRepository,
    private val autoArrangeCoordinator: AutoArrangeCoordinator,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    private var hasManualSelection = false

    init {
        viewModelScope.launch {
            autoArrangeCoordinator.refreshLayout("onboarding_init")
        }
        viewModelScope.launch {
            appInventoryProvider.observeInstalledApps().collectLatest { apps ->
                if (apps.isEmpty()) {
                    _uiState.update { it.copy(loading = true, appCount = 0) }
                } else {
                    val recommendation = recommendationEngine.recommend(apps)
                    _uiState.update {
                        it.copy(
                            loading = false,
                            appCount = apps.size,
                            recommendation = recommendation,
                            selectedSortMode = if (hasManualSelection) it.selectedSortMode else recommendation.sortMode,
                            selectedPageMode = if (hasManualSelection) it.selectedPageMode else recommendation.pageMode,
                        )
                    }
                }
            }
        }
    }

    fun selectSortMode(sortMode: SortMode) {
        hasManualSelection = true
        _uiState.update { it.copy(selectedSortMode = sortMode) }
    }

    fun selectPageMode(pageMode: PageMode) {
        hasManualSelection = true
        _uiState.update { it.copy(selectedPageMode = pageMode) }
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            val state = uiState.value
            settingsRepository.completeOnboarding(
                sortMode = state.selectedSortMode,
                pageMode = state.selectedPageMode,
            )
            autoArrangeCoordinator.refreshLayout("onboarding_complete")
            onComplete()
        }
    }
}
