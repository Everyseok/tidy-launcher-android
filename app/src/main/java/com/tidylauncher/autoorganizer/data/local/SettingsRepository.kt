package com.tidylauncher.autoorganizer.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tidylauncher.autoorganizer.domain.model.PageMode
import com.tidylauncher.autoorganizer.domain.model.PremiumEntitlement
import com.tidylauncher.autoorganizer.domain.model.SortMode
import com.tidylauncher.autoorganizer.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "tidy_settings")

class SettingsRepository(private val context: Context) {
    private object Keys {
        val onboardingCompleted = booleanPreferencesKey("onboarding_completed")
        val sortMode = stringPreferencesKey("sort_mode")
        val pageMode = stringPreferencesKey("page_mode")
        val premiumEntitlement = stringPreferencesKey("premium_entitlement")
    }

    val settings: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            onboardingCompleted = preferences[Keys.onboardingCompleted] ?: false,
            sortMode = preferences[Keys.sortMode]?.let(SortMode::valueOf) ?: SortMode.FUNCTION,
            pageMode = preferences[Keys.pageMode]?.let(PageMode::valueOf) ?: PageMode.ONE_PAGE,
            premiumEntitlement = preferences[Keys.premiumEntitlement]
                ?.let(PremiumEntitlement::valueOf)
                ?: PremiumEntitlement.LOCKED,
        )
    }

    suspend fun completeOnboarding(sortMode: SortMode, pageMode: PageMode) {
        context.dataStore.edit { preferences ->
            preferences[Keys.onboardingCompleted] = true
            preferences[Keys.sortMode] = sortMode.name
            preferences[Keys.pageMode] = pageMode.name
        }
    }

    suspend fun updateSortMode(sortMode: SortMode) {
        context.dataStore.edit { preferences ->
            preferences[Keys.sortMode] = sortMode.name
        }
    }

    suspend fun updatePageMode(pageMode: PageMode) {
        context.dataStore.edit { preferences ->
            preferences[Keys.pageMode] = pageMode.name
        }
    }

    suspend fun updatePremiumEntitlement(entitlement: PremiumEntitlement) {
        context.dataStore.edit { preferences ->
            preferences[Keys.premiumEntitlement] = entitlement.name
        }
    }
}

