package com.neo.wordsearch

import android.graphics.Paint
import android.graphics.PointF
import androidx.annotation.ColorInt

data class Line(
    val start : PointF,
    val end : PointF,
    val paint : Paint
)
