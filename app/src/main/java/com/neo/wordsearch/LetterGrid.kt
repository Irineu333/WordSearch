package com.neo.wordsearch

import android.graphics.PointF
import kotlin.math.floor

class LetterGrid(
    val letterOfLine: Int,
    private val getBoardLineSize: () -> Int
) {
    val lettersCount: Int = letterOfLine * letterOfLine
    val boardLineSize: Float get() = getBoardLineSize().toFloat()
    val letterSize: Float get() = boardLineSize / letterOfLine.toFloat()

    fun getLetterPoint(point: PointF): LetterPoint? {
        val row = floor(point.y / letterSize + 1).toInt()
        val column = floor(point.x / letterSize + 1).toInt()

        return getLetterPoint(column, row)
    }

    fun getLetterPoint(column: Int, row: Int): LetterPoint? {
        if (column < 1 || row < 1) return null
        if (column > letterOfLine || row > letterOfLine) return null
        return LetterPoint(column, row)
    }

    inner class LetterPoint(
        val column: Int,
        val row: Int
    ) {
        val end: Float = letterSize * column
        val bottom: Float = letterSize * row
        val top: Float = bottom - letterSize
        val start: Float = end - letterSize

        val center = PointF(
            end - letterSize / 2,
            bottom - letterSize / 2
        )
    }
}