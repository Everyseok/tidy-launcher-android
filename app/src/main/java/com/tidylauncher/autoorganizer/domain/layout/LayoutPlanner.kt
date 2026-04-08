package com.tidylauncher.autoorganizer.domain.layout

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import com.tidylauncher.autoorganizer.domain.FolderTitleProvider
import com.tidylauncher.autoorganizer.domain.model.AppCategory
import com.tidylauncher.autoorganizer.domain.model.FolderGroup
import com.tidylauncher.autoorganizer.domain.model.FolderItem
import com.tidylauncher.autoorganizer.domain.model.HomeAppItem
import com.tidylauncher.autoorganizer.domain.model.HomeItem
import com.tidylauncher.autoorganizer.domain.model.HomePage
import com.tidylauncher.autoorganizer.domain.model.InstalledApp
import com.tidylauncher.autoorganizer.domain.model.LayoutPlan
import com.tidylauncher.autoorganizer.domain.model.PageMode
import com.tidylauncher.autoorganizer.domain.model.PinnedSlot
import com.tidylauncher.autoorganizer.domain.model.SortMode
import com.tidylauncher.autoorganizer.domain.model.UserSettings

interface LayoutPlanner {
    fun createPlan(apps: List<InstalledApp>, settings: UserSettings): LayoutPlan
}

interface SurfaceAppResolver {
    fun resolveDockApps(apps: List<InstalledApp>): List<InstalledApp>
    fun resolvePriorityApps(apps: List<InstalledApp>, pageMode: PageMode): List<InstalledApp>
}

class CoreAppResolver(private val context: Context) : SurfaceAppResolver {
    private val packageManager = context.packageManager

    override fun resolveDockApps(apps: List<InstalledApp>): List<InstalledApp> {
        val packages = listOfNotNull(
            resolvePackage(Intent(Intent.ACTION_DIAL)),
            resolvePackage(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MESSAGING)),
            resolvePackage(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.android.com"))),
            resolvePackage(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)),
            resolvePackage(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=coffee"))),
        )

        val byPackage = apps.groupBy { it.packageName }
        val resolved = packages.mapNotNull { byPackage[it]?.firstOrNull() }
        if (resolved.isNotEmpty()) return resolved.distinctBy { it.componentName }

        return apps.filter {
            it.classifiedCategory in setOf(
                AppCategory.UTILITIES,
                AppCategory.SOCIAL,
                AppCategory.WORK,
                AppCategory.TRAVEL,
            )
        }.take(5)
    }

    override fun resolvePriorityApps(apps: List<InstalledApp>, pageMode: PageMode): List<InstalledApp> {
        val quota = if (pageMode == PageMode.ONE_PAGE) 4 else 8
        val preferredOrder = listOf(
            AppCategory.WORK,
            AppCategory.FINANCE,
            AppCategory.SOCIAL,
            AppCategory.MEDIA,
            AppCategory.TRAVEL,
            AppCategory.UTILITIES,
        )

        val selected = mutableListOf<InstalledApp>()
        preferredOrder.forEach { category ->
            apps.filter { it.classifiedCategory == category }
                .take(if (pageMode == PageMode.ONE_PAGE) 1 else 2)
                .forEach(selected::add)
        }

        return (selected + apps).distinctBy { it.componentName }.take(quota)
    }

    private fun resolvePackage(intent: Intent): String? = packageManager.resolveActivity(intent, 0)?.activityInfo?.packageName
}

class DefaultLayoutPlanner(
    private val folderNamingService: FolderTitleProvider,
    private val coreAppResolver: SurfaceAppResolver,
) : LayoutPlanner {
    override fun createPlan(apps: List<InstalledApp>, settings: UserSettings): LayoutPlan {
        if (apps.isEmpty()) {
            return LayoutPlan(
                sortMode = settings.sortMode,
                pageMode = settings.pageMode,
                recommended = false,
                dock = emptyList(),
                pages = emptyList(),
                folders = emptyList(),
                drawerSections = emptyList(),
            )
        }

        val dockApps = coreAppResolver.resolveDockApps(apps)
        val priorityApps = coreAppResolver.resolvePriorityApps(
            apps = apps.filterNot { candidate -> dockApps.any { it.componentName == candidate.componentName } },
            pageMode = settings.pageMode,
        )
        val reservedIds = (dockApps + priorityApps).map { it.componentName }.toSet()
        val groupedApps = when (settings.sortMode) {
            SortMode.FUNCTION -> apps
                .filterNot { it.componentName in reservedIds }
                .groupBy { it.classifiedCategory.name }
                .mapValues { (_, members) -> members.sortedBy { it.label.lowercase() } }
            SortMode.COLOR -> apps
                .filterNot { it.componentName in reservedIds }
                .groupBy { it.colorGroup.name }
                .mapValues { (_, members) -> members.sortedBy { it.label.lowercase() } }
        }

        val folderGroups = groupedApps.entries
            .sortedBy { it.key }
            .map { (key, members) ->
                val first = members.first()
                val title = folderNamingService.titleFor(
                    sortMode = settings.sortMode,
                    category = first.classifiedCategory,
                    colorGroup = first.colorGroup,
                )
                FolderGroup(
                    id = "folder-$key",
                    title = title,
                    apps = members,
                )
            }

        val pageItems = buildList<HomeItem> {
            priorityApps.forEach { app ->
                add(HomeAppItem(id = "app-${app.componentName}", app = app))
            }
            folderGroups.forEach { folder ->
                add(FolderItem(id = folder.id, title = folder.title, apps = folder.apps))
            }
        }

        val cappedItems = capItemsForPageMode(pageItems, settings.pageMode)
        val pageCapacity = PAGE_CAPACITY
        val pages = cappedItems
            .chunked(pageCapacity)
            .mapIndexed { index, items ->
                HomePage(index = index, items = items)
            }

        val drawerSections = buildDrawerSections(apps, SortMode.FUNCTION) + buildDrawerSections(apps, SortMode.COLOR)

        return LayoutPlan(
            sortMode = settings.sortMode,
            pageMode = settings.pageMode,
            recommended = settings.onboardingCompleted,
            dock = List(DOCK_CAPACITY) { index -> PinnedSlot(index, dockApps.getOrNull(index)) },
            pages = pages,
            folders = folderGroups,
            drawerSections = drawerSections,
        )
    }

    private fun buildDrawerSections(apps: List<InstalledApp>, sortMode: SortMode) = when (sortMode) {
        SortMode.FUNCTION -> apps.groupBy { it.classifiedCategory }
            .toSortedMap(compareBy { it.ordinal })
            .map { (category, members) ->
                com.tidylauncher.autoorganizer.domain.model.DrawerSection(
                    title = folderNamingService.titleForCategory(category),
                    sortMode = sortMode,
                    apps = members.sortedBy { it.label.lowercase() },
                )
            }
        SortMode.COLOR -> apps.groupBy { it.colorGroup }
            .toSortedMap(compareBy { it.ordinal })
            .map { (group, members) ->
                com.tidylauncher.autoorganizer.domain.model.DrawerSection(
                    title = folderNamingService.titleForColor(group),
                    sortMode = sortMode,
                    apps = members.sortedBy { it.label.lowercase() },
                )
            }
    }

    private fun capItemsForPageMode(items: List<HomeItem>, pageMode: PageMode): List<HomeItem> {
        val maxItems = PAGE_CAPACITY * if (pageMode == PageMode.ONE_PAGE) 1 else 2
        if (items.size <= maxItems) return items

        val preserved = items.take(maxItems - 1)
        val overflowApps = items.drop(maxItems - 1).flatMap { item ->
            when (item) {
                is FolderItem -> item.apps
                is HomeAppItem -> listOf(item.app)
            }
        }

        return preserved + FolderItem(
            id = "folder-overflow",
            title = folderNamingService.overflowTitle(),
            apps = overflowApps.distinctBy { it.componentName }.sortedBy { it.label.lowercase() },
        )
    }

    private companion object {
        const val DOCK_CAPACITY = 5
        const val PAGE_CAPACITY = 12
    }
}
