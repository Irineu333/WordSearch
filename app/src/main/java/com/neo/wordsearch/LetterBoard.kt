package com.neo.wordsearch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class LetterBoard(
    context: Context, attr: AttributeSet? = null
) : FrameLayout(context, attr) {

    private lateinit var letters: Array<Array<String>>

    private lateinit var letterGrid: LetterGrid

    private var letterOfLine = 0
    private val boardWidth get() = max(measuredWidth, measuredHeight)
    private var actualLine: Line? = null
    private val lines: Stack<Line> = Stack()

    var onSelectListener: OnSelectListener? = null

    init {
        setupListeners()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxMeasureSpec = max(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(maxMeasureSpec, maxMeasureSpec)

        adjustWordsSize()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (line in lines) {
            canvas.drawLine(line)
        }

        actualLine?.let { line ->
            canvas.drawLine(line)
        }
    }

    private fun setupListeners() {
        setOnTouchListener(
            object : OnTouchEvent {
                override fun down(event: MotionEvent) {
                    letterGrid.getLetterPoint(PointF(event.x, event.y))?.run {

                        val letter = letters[row - 1][column - 1]

                        val letterView: LetterView =
                            findViewWithTag("${column - 1}x${row - 1}")

                        actualLine = center.let {
                            Line(start = it, end = it, paint = getPaint(letterView.textSize))
                        }

                        onSelectListener?.selection(letter, actualLine!!.paint.color)

                        invalidate()
                    }
                }

                override fun up(event: MotionEvent) {
                    actualLine = null

                    onSelectListener?.selection("", null)

                    invalidate()
                }

                override fun move(event: MotionEvent) {
                    letterGrid.getLetterPoint(PointF(event.x, event.y))?.run {

                        val newLine = actualLine?.copy(end = center)

                        val word = newLine?.let { getWord(it) }

                        if (word != null) {
                            actualLine = newLine

                            if (onSelectListener?.selection(word, newLine.paint.color) == true) {

                                lines.add(newLine)

                                actualLine = null
                            }
                        }

                        invalidate()
                    }
                }
            }
        )
    }

    private fun getPaint(strokeWidth: Float): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = getPaintColor(lines.size)

            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
        }
    }

    @ColorInt
    private fun getPaintColor(size: Int): Int {
        return hsvToColor(getHSV(size * 10 + 10))
    }

    private fun Canvas.drawLine(
        line: Line
    ) = with(line) {
        if (isPoint) {
            drawCircle(
                start.x, start.y,
                paint.strokeWidth / 2f, paint
            )
        } else {
            drawLine(
                start.x, start.y,
                end.x, end.y,
                paint
            )
        }
    }

    private fun getWord(line: Line): String? {
        val letterA = letterGrid.getLetterPoint(line.start)
        val letterB = letterGrid.getLetterPoint(line.end)

        if (letterA == null || letterB == null) return null

        var columnDiff = letterA.column - letterB.column
        var rowDiff = letterA.row - letterB.row

        val result = StringBuilder()

        if (columnDiff != 0 && rowDiff != 0) {

            if (abs(columnDiff) != abs(rowDiff)) return null

        }

        var row = letterA.row
        var column = letterA.column

        result.append(letters[row - 1][column - 1])

        while (columnDiff != 0 || rowDiff != 0) {
            if (columnDiff != 0) {
                if (columnDiff > 0) {
                    columnDiff--
                    column--
                } else {
                    columnDiff++
                    column++
                }
            }
            if (rowDiff != 0) {
                if (rowDiff > 0) {
                    rowDiff--
                    row--
                } else {
                    rowDiff++
                    row++
                }
            }

            result.append(letters[row - 1][column - 1])
        }


        return result.toString()
    }

    private fun adjustWordsSize() {

        if (letterOfLine == 0) return

        for ((column, rows) in letters.withIndex()) {
            for ((row, word) in rows.withIndex()) {

                val wordPoint = letterGrid
                    .getLetterPoint(row + 1, column + 1)
                    ?: throw IllegalStateException("LetterView not found -> $column x $row")

                val letterView: LetterView = findViewWithTag("${column}x$row")

                letterView.x = wordPoint.start
                letterView.y = wordPoint.top

                letterView.text = word

                letterView.layoutParams.apply {

                    width = letterGrid.letterSize.toInt()
                    height = letterGrid.letterSize.toInt()

                    requestLayout()
                }
            }
        }
    }

    private fun createPuzzle(letterOfLine: Int) {

        if (letterOfLine <= 0) throw IllegalArgumentException("letterOfLine be 0 or negative")

        this.letterOfLine = letterOfLine

        letterGrid = LetterGrid(
            letterOfLine = letterOfLine,
            getBoardLineSize = { boardWidth }
        )

        removeAllViews()

        for ((column, rows) in letters.withIndex()) {
            for ((row, word) in rows.withIndex()) {
                addView(
                    LetterView(context).apply {
                        tag = "${column}x$row"
                        text = word
                    }, row
                )
            }
        }

        adjustWordsSize()
    }

    fun renderPuzzle(letters: Array<Array<String>>) {

        this.letters = letters

        if (letters.any { it.size != letters.size })
            throw IllegalArgumentException("horizontal and vertical length must be equals")

        if (letters.size != letterOfLine) {
            createPuzzle(letters.size)
        } else {

            for ((column, rows) in letters.withIndex()) {
                for ((row, word) in rows.withIndex()) {
                    setText(column, row, word)
                }
            }
        }
    }

    private fun setText(column: Int, row: Int, word: String) {
        val letterView: LetterView = findViewWithTag("${column}x$row")
        letterView.text = word
    }
}

interface OnSelectListener {
    fun selection(word: String, color: Int?): Boolean
}
