package com.tidylauncher.autoorganizer.billing

import android.content.Context
import com.tidylauncher.autoorganizer.data.local.SettingsRepository
import com.tidylauncher.autoorganizer.domain.model.PremiumEntitlement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BillingManager(
    context: Context,
    private val settingsRepository: SettingsRepository,
) {
    val premiumState: Flow<PremiumEntitlement> = settingsRepository.settings.map { it.premiumEntitlement }

    suspend fun markUnlockedForDebug() {
        settingsRepository.updatePremiumEntitlement(PremiumEntitlement.UNLOCKED)
    }

    suspend fun markLocked() {
        settingsRepository.updatePremiumEntitlement(PremiumEntitlement.LOCKED)
    }
}
