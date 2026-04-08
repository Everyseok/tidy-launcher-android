package com.tidylauncher.autoorganizer.domain.layout

import com.tidylauncher.autoorganizer.domain.FolderTitleProvider
import com.tidylauncher.autoorganizer.domain.model.AppCategory
import com.tidylauncher.autoorganizer.domain.model.AppColorGroup
import com.tidylauncher.autoorganizer.domain.model.FolderItem
import com.tidylauncher.autoorganizer.domain.model.InstalledApp
import com.tidylauncher.autoorganizer.domain.model.PageMode
import com.tidylauncher.autoorganizer.domain.model.SortMode
import com.tidylauncher.autoorganizer.domain.model.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultLayoutPlannerTest {
    private val planner = DefaultLayoutPlanner(
        folderNamingService = object : FolderTitleProvider {
            override fun titleForCategory(category: AppCategory): String = category.name.lowercase()
            override fun titleForColor(group: AppColorGroup): String = group.name.lowercase()
            override fun overflowTitle(): String = "overflow"
            override fun titleFor(
                sortMode: SortMode,
                category: AppCategory?,
                colorGroup: AppColorGroup?,
            ): String = when (sortMode) {
                SortMode.FUNCTION -> category!!.name.lowercase()
                SortMode.COLOR -> colorGroup!!.name.lowercase()
            }
        },
        coreAppResolver = object : SurfaceAppResolver {
            override fun resolveDockApps(apps: List<InstalledApp>): List<InstalledApp> = apps.take(2)
            override fun resolvePriorityApps(apps: List<InstalledApp>, pageMode: PageMode): List<InstalledApp> = apps.take(3)
        },
    )

    @Test
    fun `one page mode caps output to one page plus overflow folder`() {
        val apps = (1..30).map { index ->
            InstalledApp(
                componentName = "component/$index",
                packageName = "package.$index",
                label = "App $index",
                systemCategory = 0,
                classifiedCategory = if (index % 2 == 0) AppCategory.WORK else AppCategory.SOCIAL,
                colorGroup = if (index % 3 == 0) AppColorGroup.BLUE else AppColorGroup.RED,
                firstInstallTime = index.toLong(),
            )
        }

        val plan = planner.createPlan(
            apps = apps,
            settings = UserSettings(
                onboardingCompleted = true,
                sortMode = SortMode.FUNCTION,
                pageMode = PageMode.ONE_PAGE,
            ),
        )

        assertEquals(1, plan.pages.size)
        assertTrue(plan.pages.first().items.size <= 12)
    }

    @Test
    fun `color mode creates color drawer sections`() {
        val apps = listOf(
            testApp("a", "Alpha", AppCategory.WORK, AppColorGroup.BLUE),
            testApp("b", "Beta", AppCategory.WORK, AppColorGroup.RED),
            testApp("c", "Gamma", AppCategory.SOCIAL, AppColorGroup.RED),
        )

        val plan = planner.createPlan(
            apps = apps,
            settings = UserSettings(
                onboardingCompleted = true,
                sortMode = SortMode.COLOR,
                pageMode = PageMode.TWO_PAGES,
            ),
        )

        assertTrue(plan.drawerSections.any { it.sortMode == SortMode.COLOR && it.title == "blue" })
        assertTrue(plan.drawerSections.any { it.sortMode == SortMode.COLOR && it.title == "red" })
    }

    @Test
    fun `priority and dock apps are excluded from folders`() {
        val apps = listOf(
            testApp("a", "Alpha", AppCategory.WORK, AppColorGroup.BLUE),
            testApp("b", "Beta", AppCategory.WORK, AppColorGroup.RED),
            testApp("c", "Gamma", AppCategory.SOCIAL, AppColorGroup.RED),
            testApp("d", "Delta", AppCategory.MEDIA, AppColorGroup.YELLOW),
            testApp("e", "Epsilon", AppCategory.MEDIA, AppColorGroup.GREEN),
        )

        val plan = planner.createPlan(
            apps = apps,
            settings = UserSettings(
                onboardingCompleted = true,
                sortMode = SortMode.FUNCTION,
                pageMode = PageMode.ONE_PAGE,
            ),
        )

        val folderItems = plan.pages.flatMap { page -> page.items.filterIsInstance<FolderItem>() }
        val folderAppIds = folderItems.flatMap { it.apps }.map { it.componentName }.toSet()
        assertTrue("component/a" !in folderAppIds)
        assertTrue("component/b" !in folderAppIds)
    }

    private fun testApp(
        id: String,
        label: String,
        category: AppCategory,
        colorGroup: AppColorGroup,
    ) = InstalledApp(
        componentName = "component/$id",
        packageName = "package.$id",
        label = label,
        systemCategory = 0,
        classifiedCategory = category,
        colorGroup = colorGroup,
        firstInstallTime = 0L,
    )
}

