package com.neo.wordsearch.model

import android.graphics.Paint
import android.graphics.PointF
import androidx.annotation.ColorInt

data class Line(
    val start : PointF,
    val end : PointF,
    val paint : Paint
) {
    val isPoint get() = start.x == end.x && start.y == end.y
}
