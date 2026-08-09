package com.diabeticmonitor.app.util

import android.content.Context
import android.graphics.Color
import com.diabeticmonitor.app.R
import com.diabeticmonitor.app.data.db.entity.GlucoseStatus

object GlucoseColorUtils {

    fun getColorForStatus(status: GlucoseStatus): Int = when (status) {
        GlucoseStatus.NORMAL -> Color.parseColor("#4CAF50")  // Green
        GlucoseStatus.HIGH   -> Color.parseColor("#F44336")  // Red
        GlucoseStatus.LOW    -> Color.parseColor("#FF9800")  // Amber/Yellow
    }

    fun getColorForLevel(level: Float): Int = when {
        level < 70f  -> Color.parseColor("#FF9800")
        level > 140f -> Color.parseColor("#F44336")
        else         -> Color.parseColor("#4CAF50")
    }

    fun getLabelForStatus(status: GlucoseStatus): String = when (status) {
        GlucoseStatus.NORMAL -> "Normal"
        GlucoseStatus.HIGH   -> "High"
        GlucoseStatus.LOW    -> "Low"
    }

    fun getLabelForLevel(level: Float): String = when {
        level < 70f  -> "Low"
        level > 140f -> "High"
        else         -> "Normal"
    }

    fun getBackgroundColorForLevel(level: Float): Int = when {
        level < 70f  -> Color.parseColor("#FFF3E0")
        level > 140f -> Color.parseColor("#FFEBEE")
        else         -> Color.parseColor("#E8F5E9")
    }
}
