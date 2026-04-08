package com.tidylauncher.autoorganizer.domain.classification

import android.graphics.Bitmap
import android.graphics.Color
import androidx.palette.graphics.Palette
import com.tidylauncher.autoorganizer.domain.model.AppColorGroup

interface ColorClassifier {
    fun extractDominantColor(bitmap: Bitmap): Int
    fun classify(colorInt: Int): AppColorGroup
    fun classify(hue: Float, saturation: Float, value: Float): AppColorGroup
}

class AppColorClassifier : ColorClassifier {
    override fun extractDominantColor(bitmap: Bitmap): Int {
        val palette = Palette.from(bitmap)
            .clearFilters()
            .generate()

        return palette.dominantSwatch?.rgb ?: Color.WHITE
    }

    override fun classify(colorInt: Int): AppColorGroup {
        val hsv = FloatArray(3)
        Color.colorToHSV(colorInt, hsv)
        return classify(hsv[0], hsv[1], hsv[2])
    }

    override fun classify(hue: Float, saturation: Float, value: Float): AppColorGroup {
        if (saturation < 0.18f || value < 0.18f) return AppColorGroup.BLACK_WHITE

        return when (hue) {
            in 0f..24f,
            in 345f..360f,
            -> AppColorGroup.RED
            in 25f..44f -> AppColorGroup.ORANGE
            in 45f..69f -> AppColorGroup.YELLOW
            in 70f..159f -> AppColorGroup.GREEN
            in 160f..254f -> AppColorGroup.BLUE
            else -> AppColorGroup.PURPLE
        }
    }
}

