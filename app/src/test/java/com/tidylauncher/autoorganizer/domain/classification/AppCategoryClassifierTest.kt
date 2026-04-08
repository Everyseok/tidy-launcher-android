package com.tidylauncher.autoorganizer.domain.classification

import android.content.pm.ApplicationInfo
import com.tidylauncher.autoorganizer.domain.model.AppCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class AppCategoryClassifierTest {
    private val classifier = AppCategoryClassifier()

    @Test
    fun `maps system category becomes travel`() {
        val result = classifier.classify(
            label = "Maps",
            packageName = "com.google.android.apps.maps",
            systemCategory = ApplicationInfo.CATEGORY_MAPS,
        )

        assertEquals(AppCategory.TRAVEL, result)
    }

    @Test
    fun `financial keywords become finance`() {
        val result = classifier.classify(
            label = "Toss Bank",
            packageName = "com.tossbank.app",
            systemCategory = ApplicationInfo.CATEGORY_UNDEFINED,
        )

        assertEquals(AppCategory.FINANCE, result)
    }

    @Test
    fun `work keywords become work`() {
        val result = classifier.classify(
            label = "Notion Calendar",
            packageName = "notion.calendar.app",
            systemCategory = ApplicationInfo.CATEGORY_UNDEFINED,
        )

        assertEquals(AppCategory.WORK, result)
    }
}

