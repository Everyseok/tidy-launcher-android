package com.tidylauncher.autoorganizer.domain

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Color
import com.tidylauncher.autoorganizer.domain.model.InstalledApp
import com.tidylauncher.autoorganizer.domain.model.PageMode
import com.tidylauncher.autoorganizer.domain.model.RecommendationReason
import com.tidylauncher.autoorganizer.domain.model.RecommendedPlan
import com.tidylauncher.autoorganizer.domain.model.SortMode

interface RecommendationEngine {
    fun recommend(apps: List<InstalledApp>): RecommendedPlan
}

class DefaultRecommendationEngine(context: Context) : RecommendationEngine {
    private val wallpaperManager = WallpaperManager.getInstance(context)

    override fun recommend(apps: List<InstalledApp>): RecommendedPlan {
        val categorySpread = apps.map { it.classifiedCategory }.distinct().size
        val wallpaperArgb = wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
            ?.primaryColor
            ?.toArgb()
            ?: Color.WHITE
        val hsv = FloatArray(3)
        Color.colorToHSV(wallpaperArgb, hsv)

        val sortMode = when {
            hsv[1] < 0.16f -> SortMode.COLOR
            categorySpread >= 6 -> SortMode.FUNCTION
            else -> SortMode.COLOR
        }

        val pageMode = if (apps.size >= 36) PageMode.ONE_PAGE else PageMode.TWO_PAGES

        return RecommendedPlan(
            sortMode = sortMode,
            pageMode = pageMode,
            sortReason = if (sortMode == SortMode.FUNCTION) {
                RecommendationReason.FUNCTION_SPREAD
            } else {
                RecommendationReason.COLOR_BALANCE
            },
            pageReason = if (pageMode == PageMode.ONE_PAGE) {
                RecommendationReason.ONE_PAGE_DENSITY
            } else {
                RecommendationReason.TWO_PAGE_BREATHING_ROOM
            },
        )
    }
}

