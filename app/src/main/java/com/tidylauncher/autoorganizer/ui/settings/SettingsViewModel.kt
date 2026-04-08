package com.tidylauncher.autoorganizer.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tidylauncher.autoorganizer.billing.BillingManager
import com.tidylauncher.autoorganizer.data.local.SettingsRepository
import com.tidylauncher.autoorganizer.domain.layout.AutoArrangeCoordinator
import com.tidylauncher.autoorganizer.domain.model.PageMode
import com.tidylauncher.autoorganizer.domain.model.PremiumEntitlement
import com.tidylauncher.autoorganizer.domain.model.SortMode
import com.tidylauncher.autoorganizer.domain.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val billingManager: BillingManager,
    private val autoArrangeCoordinator: AutoArrangeCoordinator,
) : ViewModel() {
    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collectLatest { _settings.value = it }
        }
    }

    fun updateSortMode(mode: SortMode) {
        viewModelScope.launch {
            settingsRepository.updateSortMode(mode)
            autoArrangeCoordinator.refreshLayout("settings_sort_mode")
        }
    }

    fun updatePageMode(mode: PageMode) {
        viewModelScope.launch {
            settingsRepository.updatePageMode(mode)
            autoArrangeCoordinator.refreshLayout("settings_page_mode")
        }
    }

    fun rerun() {
        viewModelScope.launch {
            autoArrangeCoordinator.refreshLayout("settings_rerun")
        }
    }

    fun unlockPremium() {
        viewModelScope.launch {
            billingManager.markUnlockedForDebug()
        }
    }

    fun lockPremium() {
        viewModelScope.launch {
            billingManager.markLocked()
        }
    }
}

