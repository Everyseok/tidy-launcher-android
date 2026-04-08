package com.tidylauncher.autoorganizer.ui.launcher

import android.content.pm.PackageManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tidylauncher.autoorganizer.R
import com.tidylauncher.autoorganizer.domain.model.DrawerSection
import com.tidylauncher.autoorganizer.domain.model.DrawerTabState
import com.tidylauncher.autoorganizer.domain.model.FolderItem
import com.tidylauncher.autoorganizer.domain.model.HomeAppItem
import com.tidylauncher.autoorganizer.domain.model.HomeItem
import com.tidylauncher.autoorganizer.domain.model.InstalledApp
import com.tidylauncher.autoorganizer.domain.model.SortMode
import com.tidylauncher.autoorganizer.ui.common.AppIcon

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LauncherScreen(
    state: LauncherUiState,
    packageManager: PackageManager,
    onQueryChange: (String) -> Unit,
    onDrawerTabChange: (DrawerTabState) -> Unit,
    onLaunchApp: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onRefresh: () -> Unit,
) {
    var selectedTopTab by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { state.plan?.pages?.size ?: 0 },
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Row {
                        IconButton(onClick = onRefresh) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null)
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(Icons.Rounded.Settings, contentDescription = null)
                        }
                    }
                }
                OutlinedTextField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = stringResource(R.string.search_hint)) },
                    singleLine = true,
                )
                TabRow(selectedTabIndex = selectedTopTab) {
                    listOf(
                        stringResource(R.string.home_title),
                        stringResource(R.string.drawer_title),
                    ).forEachIndexed { index, label ->
                        Tab(
                            selected = selectedTopTab == index,
                            onClick = { selectedTopTab = index },
                            text = { Text(text = label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        when {
            state.loading -> CenterStatus(stringResource(R.string.status_loading_apps), Modifier.padding(padding))
            state.query.isNotBlank() -> SearchResults(
                apps = state.searchResults,
                packageManager = packageManager,
                onLaunchApp = onLaunchApp,
                modifier = Modifier.padding(padding),
            )
            selectedTopTab == 0 -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DockRow(
                    apps = state.plan?.dock?.mapNotNull { it.app }.orEmpty(),
                    packageManager = packageManager,
                    onLaunchApp = onLaunchApp,
                )
                if ((state.plan?.pages?.size ?: 0) > 0) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f),
                    ) { page ->
                        HomePageGrid(
                            items = state.plan?.pages?.getOrNull(page)?.items.orEmpty(),
                            packageManager = packageManager,
                            onLaunchApp = onLaunchApp,
                        )
                    }
                } else {
                    CenterStatus(stringResource(R.string.status_empty_drawer), Modifier.weight(1f))
                }
            }
            else -> DrawerScreen(
                sections = state.plan.orEmptySections(state.drawerTabState),
                drawerTabState = state.drawerTabState,
                packageManager = packageManager,
                onDrawerTabChange = onDrawerTabChange,
                onLaunchApp = onLaunchApp,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun DockRow(
    apps: List<InstalledApp>,
    packageManager: PackageManager,
    onLaunchApp: (String) -> Unit,
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            apps.forEach { app ->
                AppTile(
                    app = app,
                    packageManager = packageManager,
                    onLaunchApp = onLaunchApp,
                    compact = true,
                )
            }
        }
    }
}

@Composable
private fun HomePageGrid(
    items: List<HomeItem>,
    packageManager: PackageManager,
    onLaunchApp: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items, key = { it.id }) { item ->
            when (item) {
                is FolderItem -> FolderTile(item, packageManager, onLaunchApp)
                is HomeAppItem -> AppTile(item.app, packageManager, onLaunchApp)
            }
        }
    }
}

@Composable
private fun FolderTile(
    item: FolderItem,
    packageManager: PackageManager,
    onLaunchApp: (String) -> Unit,
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = item.title, fontWeight = FontWeight.SemiBold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(item.apps.take(6), key = { it.componentName }) { app ->
                    AppIcon(
                        packageManager = packageManager,
                        componentName = app.componentName,
                        modifier = Modifier.clickable { onLaunchApp(app.componentName) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppTile(
    app: InstalledApp,
    packageManager: PackageManager,
    onLaunchApp: (String) -> Unit,
    compact: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLaunchApp(app.componentName) }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        AppIcon(packageManager = packageManager, componentName = app.componentName)
        Text(
            text = app.label,
            textAlign = TextAlign.Center,
            style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
            maxLines = 2,
        )
    }
}

@Composable
private fun DrawerScreen(
    sections: List<DrawerSection>,
    drawerTabState: DrawerTabState,
    packageManager: PackageManager,
    onDrawerTabChange: (DrawerTabState) -> Unit,
    onLaunchApp: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(
                selected = drawerTabState == DrawerTabState.FUNCTION,
                onClick = { onDrawerTabChange(DrawerTabState.FUNCTION) },
                label = { Text(stringResource(R.string.drawer_tab_function)) },
            )
            FilterChip(
                selected = drawerTabState == DrawerTabState.COLOR,
                onClick = { onDrawerTabChange(DrawerTabState.COLOR) },
                label = { Text(stringResource(R.string.drawer_tab_color)) },
            )
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(sections.size, key = { sections[it].title + sections[it].sortMode.name }) { index ->
                val section = sections[index]
                Card {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(text = section.title, fontWeight = FontWeight.SemiBold)
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier.height(180.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(section.apps, key = { it.componentName }) { app ->
                                AppTile(
                                    app = app,
                                    packageManager = packageManager,
                                    onLaunchApp = onLaunchApp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResults(
    apps: List<InstalledApp>,
    packageManager: PackageManager,
    onLaunchApp: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (apps.isEmpty()) {
        CenterStatus(stringResource(R.string.status_no_results), modifier.fillMaxSize())
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(apps, key = { it.componentName }) { app ->
            AppTile(app = app, packageManager = packageManager, onLaunchApp = onLaunchApp)
        }
    }
}

@Composable
private fun CenterStatus(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun com.tidylauncher.autoorganizer.domain.model.LayoutPlan?.orEmptySections(
    tabState: DrawerTabState,
): List<DrawerSection> = this?.drawerSections?.filter {
    when (tabState) {
        DrawerTabState.FUNCTION -> it.sortMode == SortMode.FUNCTION
        DrawerTabState.COLOR -> it.sortMode == SortMode.COLOR
    }
}.orEmpty()
