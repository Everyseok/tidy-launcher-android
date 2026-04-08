package com.tidylauncher.autoorganizer.domain.model

enum class SortMode {
    FUNCTION,
    COLOR,
}

enum class PageMode {
    ONE_PAGE,
    TWO_PAGES,
}

enum class AppCategory {
    SOCIAL,
    WORK,
    FINANCE,
    SHOPPING,
    MEDIA,
    TRAVEL,
    UTILITIES,
    GAMES,
    HEALTH,
    TOOLS,
}

enum class AppColorGroup {
    RED,
    ORANGE,
    YELLOW,
    GREEN,
    BLUE,
    PURPLE,
    BLACK_WHITE,
}

enum class DrawerTabState {
    FUNCTION,
    COLOR,
}

enum class PremiumEntitlement {
    LOCKED,
    UNLOCKED,
}

enum class RecommendationReason {
    FUNCTION_SPREAD,
    COLOR_BALANCE,
    ONE_PAGE_DENSITY,
    TWO_PAGE_BREATHING_ROOM,
}

data class UserSettings(
    val onboardingCompleted: Boolean = false,
    val sortMode: SortMode = SortMode.FUNCTION,
    val pageMode: PageMode = PageMode.ONE_PAGE,
    val premiumEntitlement: PremiumEntitlement = PremiumEntitlement.LOCKED,
)

data class RecommendedPlan(
    val sortMode: SortMode,
    val pageMode: PageMode,
    val sortReason: RecommendationReason,
    val pageReason: RecommendationReason,
)

data class InstalledApp(
    val componentName: String,
    val packageName: String,
    val label: String,
    val systemCategory: Int,
    val classifiedCategory: AppCategory,
    val colorGroup: AppColorGroup,
    val firstInstallTime: Long,
)

data class FolderGroup(
    val id: String,
    val title: String,
    val apps: List<InstalledApp>,
)

data class PinnedSlot(
    val position: Int,
    val app: InstalledApp?,
)

sealed interface HomeItem {
    val id: String
}

data class HomeAppItem(
    override val id: String,
    val app: InstalledApp,
) : HomeItem

data class FolderItem(
    override val id: String,
    val title: String,
    val apps: List<InstalledApp>,
) : HomeItem

data class HomePage(
    val index: Int,
    val items: List<HomeItem>,
)

data class DrawerSection(
    val title: String,
    val sortMode: SortMode,
    val apps: List<InstalledApp>,
)

data class LayoutPlan(
    val sortMode: SortMode,
    val pageMode: PageMode,
    val recommended: Boolean,
    val dock: List<PinnedSlot>,
    val pages: List<HomePage>,
    val folders: List<FolderGroup>,
    val drawerSections: List<DrawerSection>,
)

data class LayoutSnapshot(
    val plan: LayoutPlan,
    val createdAt: Long,
)

