package com.neo.wordsearch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import java.util.*
import kotlin.math.abs

class LetterBoard(
    context: Context, attr: AttributeSet? = null
) : FrameLayout(context, attr) {

    private lateinit var letters: Array<Array<String>>
    private lateinit var linePaint: Paint
    private lateinit var letterGrid: LetterGrid

    private var letterOfLine = 0
    private val boardWidth get() = measuredWidth
    private var actualLine: Pair<PointF, PointF>? = null
    private val lines : Stack<Pair<PointF, PointF>> = Stack()

    var onSelectListener: OnSelectListener? = null

    init {
        setupListeners()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

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

                        actualLine = center.let { it to it }

                        val letterView: LetterView =
                            findViewWithTag("${column - 1}x${row - 1}")

                        linePaint = Paint().apply {
                            color = Color.RED

                            strokeWidth = letterView.textSize
                            strokeCap = Paint.Cap.ROUND
                        }

                        onSelectListener?.selectWord(letter, linePaint.color)

                        invalidate()
                    }
                }

                override fun up(event: MotionEvent) {
                    actualLine = null

                    onSelectListener?.selectWord("", linePaint.color)

                    invalidate()
                }

                override fun move(event: MotionEvent) {
                    letterGrid.getLetterPoint(PointF(event.x, event.y))?.run {

                        val newLine = actualLine?.copy(second = center)

                        val word = newLine?.let { getWord(it) }

                        if (word != null) {
                            actualLine = newLine

                            if(onSelectListener?.selectWord(word, linePaint.color) == true) {
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

    private fun getWord(actualLine: Pair<PointF, PointF>): String? {
        val letterA = letterGrid.getLetterPoint(actualLine.first)
        val letterB = letterGrid.getLetterPoint(actualLine.second)

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

    private fun Canvas.drawLine(
        line: Pair<PointF, PointF>
    ) = with(line) {
        drawLine(
            first.x, first.y,
            second.x, second.y,
            linePaint
        )
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
    fun selectWord(word: String, color: Int): Boolean
}
