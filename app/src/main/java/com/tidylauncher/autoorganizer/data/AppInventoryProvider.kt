package com.tidylauncher.autoorganizer.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.os.Process
import androidx.core.graphics.drawable.toBitmap
import com.tidylauncher.autoorganizer.data.local.InstalledAppDao
import com.tidylauncher.autoorganizer.data.local.InstalledAppEntity
import com.tidylauncher.autoorganizer.data.local.toModel
import com.tidylauncher.autoorganizer.domain.classification.CategoryClassifier
import com.tidylauncher.autoorganizer.domain.classification.ColorClassifier
import com.tidylauncher.autoorganizer.domain.model.InstalledApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface AppInventoryProvider {
    fun observeInstalledApps(): Flow<List<InstalledApp>>
    suspend fun refreshInventory(): List<InstalledApp>
    fun launchIntentFor(componentName: String): Intent?
}

class DefaultAppInventoryProvider(
    private val context: Context,
    private val appDao: InstalledAppDao,
    private val categoryClassifier: CategoryClassifier,
    private val colorClassifier: ColorClassifier,
) : AppInventoryProvider {
    private val launcherApps = context.getSystemService(LauncherApps::class.java)
    private val packageManager = context.packageManager

    override fun observeInstalledApps(): Flow<List<InstalledApp>> = appDao.observeAll().map { entities ->
        entities.map { it.toModel() }
    }

    override suspend fun refreshInventory(): List<InstalledApp> = withContext(Dispatchers.Default) {
        val entries = launcherApps
            ?.getActivityList(null, Process.myUserHandle())
            .orEmpty()
            .filterNot { it.applicationInfo.packageName == context.packageName }
            .distinctBy { it.componentName.flattenToShortString() }
            .map { it.toEntity() }

        if (entries.isEmpty()) {
            appDao.clear()
            return@withContext emptyList()
        }

        appDao.upsertAll(entries)
        appDao.deleteMissing(entries.map { it.componentName })
        entries.map { it.toModel() }
    }

    private suspend fun LauncherActivityInfo.toEntity(): InstalledAppEntity {
        val category = categoryClassifier.classify(
            label = label?.toString().orEmpty(),
            packageName = applicationInfo.packageName,
            systemCategory = applicationInfo.category,
        )
        val group = colorClassifier.classify(
            colorInt = colorClassifier.extractDominantColor(getIcon(0).toBitmap()),
        )

        return InstalledAppEntity(
            componentName = componentName.flattenToString(),
            packageName = applicationInfo.packageName,
            label = label?.toString().orEmpty(),
            systemCategory = applicationInfo.category,
            classifiedCategory = category.name,
            colorGroup = group.name,
            firstInstallTime = firstInstallTime,
        )
    }

    override fun launchIntentFor(componentName: String): Intent? = ComponentName.unflattenFromString(componentName)?.let {
        packageManager.getLaunchIntentForPackage(it.packageName)?.apply {
            component = it
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
