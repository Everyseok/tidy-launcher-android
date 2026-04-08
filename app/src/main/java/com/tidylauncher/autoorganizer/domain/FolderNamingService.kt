package com.tidylauncher.autoorganizer.domain

import android.content.Context
import com.tidylauncher.autoorganizer.R
import com.tidylauncher.autoorganizer.domain.model.AppCategory
import com.tidylauncher.autoorganizer.domain.model.AppColorGroup
import com.tidylauncher.autoorganizer.domain.model.SortMode
import java.util.Locale

interface FolderTitleProvider {
    fun titleForCategory(category: AppCategory): String
    fun titleForColor(group: AppColorGroup): String
    fun overflowTitle(): String
    fun titleFor(sortMode: SortMode, category: AppCategory?, colorGroup: AppColorGroup?): String
}

class FolderNamingService(private val context: Context) : FolderTitleProvider {
    override fun titleForCategory(category: AppCategory): String {
        val korean = Locale.getDefault().language.startsWith("ko")
        return when (category) {
            AppCategory.SOCIAL -> if (korean) "소셜" else "Social"
            AppCategory.WORK -> if (korean) "업무" else "Work"
            AppCategory.FINANCE -> if (korean) "금융" else "Finance"
            AppCategory.SHOPPING -> if (korean) "쇼핑" else "Shopping"
            AppCategory.MEDIA -> if (korean) "미디어" else "Media"
            AppCategory.TRAVEL -> if (korean) "이동" else "Travel"
            AppCategory.UTILITIES -> if (korean) "유틸리티" else "Utilities"
            AppCategory.GAMES -> if (korean) "게임" else "Games"
            AppCategory.HEALTH -> if (korean) "건강" else "Health"
            AppCategory.TOOLS -> if (korean) "도구" else "Tools"
        }
    }

    override fun titleForColor(group: AppColorGroup): String {
        val korean = Locale.getDefault().language.startsWith("ko")
        return when (group) {
            AppColorGroup.RED -> if (korean) "레드" else "Red"
            AppColorGroup.ORANGE -> if (korean) "오렌지" else "Orange"
            AppColorGroup.YELLOW -> if (korean) "옐로" else "Yellow"
            AppColorGroup.GREEN -> if (korean) "그린" else "Green"
            AppColorGroup.BLUE -> if (korean) "블루" else "Blue"
            AppColorGroup.PURPLE -> if (korean) "퍼플" else "Purple"
            AppColorGroup.BLACK_WHITE -> if (korean) "모노" else "Mono"
        }
    }

    override fun overflowTitle(): String = context.getString(R.string.folder_title_fallback)

    override fun titleFor(sortMode: SortMode, category: AppCategory?, colorGroup: AppColorGroup?): String = when (sortMode) {
        SortMode.FUNCTION -> titleForCategory(checkNotNull(category))
        SortMode.COLOR -> titleForColor(checkNotNull(colorGroup))
    }
}
