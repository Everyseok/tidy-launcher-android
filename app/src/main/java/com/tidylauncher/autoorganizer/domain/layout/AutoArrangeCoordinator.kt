package com.tidylauncher.autoorganizer.domain.layout

import com.tidylauncher.autoorganizer.data.AppInventoryProvider
import com.tidylauncher.autoorganizer.data.local.SettingsRepository
import com.tidylauncher.autoorganizer.domain.model.LayoutPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

interface AutoArrangeCoordinator {
    fun observeLayoutPlan(): Flow<LayoutPlan>
    suspend fun refreshLayout(reason: String)
}

class DefaultAutoArrangeCoordinator(
    private val appInventoryProvider: AppInventoryProvider,
    private val settingsRepository: SettingsRepository,
    private val layoutPlanner: LayoutPlanner,
) : AutoArrangeCoordinator {
    override fun observeLayoutPlan(): Flow<LayoutPlan> = combine(
        appInventoryProvider.observeInstalledApps(),
        settingsRepository.settings,
    ) { apps, settings ->
        layoutPlanner.createPlan(apps = apps, settings = settings)
    }.map { it }

    override suspend fun refreshLayout(reason: String) {
        appInventoryProvider.refreshInventory()
    }
}

