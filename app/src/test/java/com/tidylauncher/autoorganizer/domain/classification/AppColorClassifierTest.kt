package com.tidylauncher.autoorganizer.domain.classification

import com.tidylauncher.autoorganizer.domain.model.AppColorGroup
import org.junit.Assert.assertEquals
import org.junit.Test

class AppColorClassifierTest {
    private val classifier = AppColorClassifier()

    @Test
    fun `low saturation maps to black white`() {
        assertEquals(
            AppColorGroup.BLACK_WHITE,
            classifier.classify(hue = 0f, saturation = 0.08f, value = 0.94f),
        )
    }

    @Test
    fun `green hue maps to green group`() {
        assertEquals(
            AppColorGroup.GREEN,
            classifier.classify(hue = 120f, saturation = 0.8f, value = 0.9f),
        )
    }

    @Test
    fun `blue hue maps to blue group`() {
        assertEquals(
            AppColorGroup.BLUE,
            classifier.classify(hue = 220f, saturation = 0.7f, value = 0.9f),
        )
    }
}

