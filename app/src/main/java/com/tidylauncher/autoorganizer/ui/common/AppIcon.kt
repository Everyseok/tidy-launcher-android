package com.tidylauncher.autoorganizer.ui.common

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap

@Composable
fun AppIcon(
    packageManager: PackageManager,
    componentName: String,
    modifier: Modifier = Modifier,
) {
    val imageBitmap = remember(componentName) {
        val component = ComponentName.unflattenFromString(componentName)
        val drawable = try {
            component?.let { packageManager.getActivityIcon(it) }
        } catch (_: Throwable) {
            null
        } ?: packageManager.defaultActivityIcon

        drawable.toBitmap(128, 128).asImageBitmap()
    }

    Box(
        modifier = modifier
            .size(56.dp)
            .background(
                color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(18.dp),
            ),
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
        )
    }
}

