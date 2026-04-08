package com.tidylauncher.autoorganizer.domain.classification

import android.content.pm.ApplicationInfo
import com.tidylauncher.autoorganizer.domain.model.AppCategory
import java.util.Locale

interface CategoryClassifier {
    fun classify(label: String, packageName: String, systemCategory: Int): AppCategory
}

class AppCategoryClassifier : CategoryClassifier {
    override fun classify(label: String, packageName: String, systemCategory: Int): AppCategory {
        if (systemCategory != ApplicationInfo.CATEGORY_UNDEFINED) {
            return when (systemCategory) {
                ApplicationInfo.CATEGORY_GAME -> AppCategory.GAMES
                ApplicationInfo.CATEGORY_AUDIO,
                ApplicationInfo.CATEGORY_VIDEO,
                ApplicationInfo.CATEGORY_IMAGE,
                ApplicationInfo.CATEGORY_NEWS,
                -> AppCategory.MEDIA
                ApplicationInfo.CATEGORY_SOCIAL -> AppCategory.SOCIAL
                ApplicationInfo.CATEGORY_PRODUCTIVITY -> AppCategory.WORK
                ApplicationInfo.CATEGORY_MAPS -> AppCategory.TRAVEL
                ApplicationInfo.CATEGORY_ACCESSIBILITY -> AppCategory.TOOLS
                else -> AppCategory.UTILITIES
            }
        }

        val haystack = "${label.lowercase(Locale.ROOT)} ${packageName.lowercase(Locale.ROOT)}"

        return when {
            haystack.hasAny("bank", "pay", "card", "wallet", "finance", "stock", "coin", "토스", "뱅크", "증권", "은행") -> AppCategory.FINANCE
            haystack.hasAny("shop", "store", "market", "mart", "amazon", "coupang", "11st", "무신사", "shopping") -> AppCategory.SHOPPING
            haystack.hasAny("map", "maps", "taxi", "travel", "flight", "trip", "hotel", "지하철", "지도", "여행", "배달") -> AppCategory.TRAVEL
            haystack.hasAny("youtube", "music", "netflix", "photo", "gallery", "camera", "podcast", "video", "movie", "사진") -> AppCategory.MEDIA
            haystack.hasAny("slack", "docs", "drive", "mail", "calendar", "notion", "office", "work", "zoom", "회의", "메모") -> AppCategory.WORK
            haystack.hasAny("talk", "chat", "message", "telegram", "discord", "sns", "social", "카톡", "인스타", "페북", "x.com") -> AppCategory.SOCIAL
            haystack.hasAny("health", "fit", "run", "sleep", "med", "병원", "건강") -> AppCategory.HEALTH
            haystack.hasAny("tool", "setting", "files", "vpn", "auth", "password", "scan", "utility", "설정", "파일") -> AppCategory.TOOLS
            else -> AppCategory.UTILITIES
        }
    }

    private fun String.hasAny(vararg values: String): Boolean = values.any { contains(it) }
}
