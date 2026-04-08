package com.tidylauncher.autoorganizer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tidylauncher.autoorganizer.domain.model.AppCategory
import com.tidylauncher.autoorganizer.domain.model.AppColorGroup
import com.tidylauncher.autoorganizer.domain.model.InstalledApp

@Entity(tableName = "installed_apps")
data class InstalledAppEntity(
    @PrimaryKey val componentName: String,
    val packageName: String,
    val label: String,
    val systemCategory: Int,
    val classifiedCategory: String,
    val colorGroup: String,
    val firstInstallTime: Long,
)

fun InstalledAppEntity.toModel(): InstalledApp = InstalledApp(
    componentName = componentName,
    packageName = packageName,
    label = label,
    systemCategory = systemCategory,
    classifiedCategory = AppCategory.valueOf(classifiedCategory),
    colorGroup = AppColorGroup.valueOf(colorGroup),
    firstInstallTime = firstInstallTime,
)

