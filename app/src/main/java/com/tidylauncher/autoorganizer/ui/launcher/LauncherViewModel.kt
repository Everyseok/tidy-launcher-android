package com.tidylauncher.autoorganizer.ui.launcher

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tidylauncher.autoorganizer.billing.BillingManager
import com.tidylauncher.autoorganizer.data.AppInventoryProvider
import com.tidylauncher.autoorganizer.data.local.SettingsRepository
import com.tidylauncher.autoorganizer.domain.layout.AutoArrangeCoordinator
import com.tidylauncher.autoorganizer.domain.model.DrawerSection
import com.tidylauncher.autoorganizer.domain.model.DrawerTabState
import com.tidylauncher.autoorganizer.domain.model.InstalledApp
import com.tidylauncher.autoorganizer.domain.model.LayoutPlan
import com.tidylauncher.autoorganizer.domain.model.PageMode
import com.tidylauncher.autoorganizer.domain.model.PremiumEntitlement
import com.tidylauncher.autoorganizer.domain.model.SortMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LauncherUiState(
    val loading: Boolean = true,
    val needsOnboarding: Boolean = false,
    val plan: LayoutPlan? = null,
    val query: String = "",
    val searchResults: List<InstalledApp> = emptyList(),
    val drawerTabState: DrawerTabState = DrawerTabState.FUNCTION,
    val premiumEntitlement: PremiumEntitlement = PremiumEntitlement.LOCKED,
    val sortMode: SortMode = SortMode.FUNCTION,
    val pageMode: PageMode = PageMode.ONE_PAGE,
)

class LauncherViewModel(
    private val appInventoryProvider: AppInventoryProvider,
    private val settingsRepository: SettingsRepository,
    private val autoArrangeCoordinator: AutoArrangeCoordinator,
    private val billingManager: BillingManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState: StateFlow<LauncherUiState> = _uiState.asStateFlow()
    private var cachedApps: List<InstalledApp> = emptyList()

    init {
        viewModelScope.launch {
            autoArrangeCoordinator.refreshLayout("launcher_init")
        }
        viewModelScope.launch {
            combine(
                settingsRepository.settings,
                autoArrangeCoordinator.observeLayoutPlan(),
                appInventoryProvider.observeInstalledApps(),
                billingManager.premiumState,
            ) { settings, plan, apps, premium ->
                Quadruple(settings, plan, apps, premium)
            }.collectLatest { (settings, plan, apps, premium) ->
                cachedApps = apps
                val query = _uiState.value.query
                _uiState.update {
                    it.copy(
                        loading = false,
                        needsOnboarding = !settings.onboardingCompleted,
                        plan = plan,
                        searchResults = apps.filterByQuery(query),
                        premiumEntitlement = premium,
                        sortMode = settings.sortMode,
                        pageMode = settings.pageMode,
                    )
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            autoArrangeCoordinator.refreshLayout("manual_refresh")
        }
    }

    fun updateQuery(query: String) {
        _uiState.update {
            it.copy(
                query = query,
                searchResults = cachedApps.filterByQuery(query),
            )
        }
    }

    fun updateDrawerTab(tab: DrawerTabState) {
        _uiState.update { it.copy(drawerTabState = tab) }
    }

    fun launchIntentFor(componentName: String): Intent? = appInventoryProvider.launchIntentFor(componentName)

    private fun List<InstalledApp>.filterByQuery(query: String): List<InstalledApp> {
        if (query.isBlank()) return emptyList()
        return filter { app ->
            app.label.contains(query, ignoreCase = true) ||
                app.packageName.contains(query, ignoreCase = true)
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
)
