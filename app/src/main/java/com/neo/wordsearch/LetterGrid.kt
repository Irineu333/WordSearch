package com.neo.wordsearch

import android.graphics.PointF
import timber.log.Timber
import kotlin.math.floor

class LetterGrid(
    val letterOfLine: Int,
    private val getBoardLineSize: () -> Int
) {
    val lettersCount = letterOfLine * letterOfLine
    val boardLineSize get() = getBoardLineSize()
    val letterSize: Float get() = boardLineSize.toFloat() / letterOfLine

    fun getLetterPoint(point: PointF): LetterPoint? {
        val row = floor(point.y / letterSize).toInt() + 1
        val column = floor(point.x / letterSize).toInt() + 1

        Timber.i("word $column x $row")

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
        val end = (letterSize * column)
        val bottom = (letterSize * row)
        val top = bottom - letterSize
        val start = end - letterSize

        val center = PointF(
            end - letterSize / 2f,
            bottom - letterSize / 2f
        )
    }
}