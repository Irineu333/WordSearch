package com.neo.wordsearch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import timber.log.Timber
import kotlin.math.floor


class WordBoard(
    context: Context, attr: AttributeSet? = null
) : FrameLayout(context, attr) {

    private lateinit var words: Array<Array<String>>

    private var wordLineCount = 0
    private val boardLineSize get() = measuredWidth

    private lateinit var grid: WordGrid

    private var actualLine: Pair<PointF, PointF>? = null

    private lateinit var paint: Paint

    init {
        setupListeners()
    }

    private fun setupListeners() {
        setOnTouchListener(
            object : OnTouchEvent {
                override fun down(event: MotionEvent) {
                    val wordPoint = grid.getWordPoint(PointF(event.x, event.y))

                    Timber.i(
                        "down word\n" +
                                "center point ${wordPoint.center.x} x ${wordPoint.center.y}"
                    )

                    actualLine = wordPoint.center.let { it to it }

                    invalidate()
                }

                override fun up(event: MotionEvent) {
                    actualLine = null

                    invalidate()
                }

                override fun move(event: MotionEvent) {
                    val wordPoint = grid.getWordPoint(PointF(event.x, event.y))

                    Timber.i(
                        "move word\n" +
                                "center point ${wordPoint.center.x} x ${wordPoint.center.y}"
                    )
                    actualLine = actualLine?.copy(second = wordPoint.center)

                    invalidate()
                }
            }
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

        adjustWordsSize()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        actualLine?.let {
            canvas.drawLine(it)
        }
    }

    private fun Canvas.drawLine(
        line: Pair<PointF, PointF>
    ) = with(line) {
        drawLine(
            first.x, first.y,
            second.x, second.y,
            paint
        )
    }

    private fun adjustWordsSize() {

        if (wordLineCount == 0) return

        Timber.i(
            "adjust size\nword size -> ${grid.wordSize}\n" +
                    "board size -> $boardLineSize X $boardLineSize"
        )

        for ((column, rows) in words.withIndex()) {
            for ((row, word) in rows.withIndex()) {
                val wordPoint = grid.getWordPoint(row + 1, column + 1)

                val wordView: WordView = findViewWithTag("${column}x$row")

                wordView.x = wordPoint.start.toFloat()
                wordView.y = wordPoint.top.toFloat()

                wordView.text = word

                wordView.layoutParams.apply {

                    width = grid.wordSize.toInt()
                    height = grid.wordSize.toInt()

                    requestLayout()
                }
            }
        }

        paint = Paint().apply {
            color = Color.RED
            strokeWidth = grid.wordSize * 0.75f
            strokeCap = Paint.Cap.ROUND

            textSize = grid.wordSize * 0.75f
        }
    }

    private fun createPuzzle(wordLineCount: Int) {

        Timber.i("create puzzle $wordLineCount x $wordLineCount")

        if (wordLineCount <= 0) throw IllegalArgumentException("length not be 0 or negative")

        this.wordLineCount = wordLineCount

        grid = WordGrid(
            wordLineCount = wordLineCount,
            getBoardLineSize = { boardLineSize }
        )

        removeAllViews()

        for ((column, rows) in words.withIndex()) {
            for ((row, word) in rows.withIndex()) {
                addView(
                    WordView(context).apply {
                        tag = "${column}x$row"
                        text = word
                    }, row
                )
            }
        }

        adjustWordsSize()
    }

    fun renderPuzzle(words: Array<Array<String>>) {

        this.words = words

        Timber.i("create puzzle $words x $words")

        if (words.any { it.size != words.size })
            throw IllegalArgumentException("horizontal and vertical length must be equals")

        if (words.size != wordLineCount) {
            createPuzzle(words.size)
        } else {

            for ((column, rows) in words.withIndex()) {
                for ((row, word) in rows.withIndex()) {
                    setText(column, row, word)
                }
            }

        }
    }

    private fun setText(column: Int, row: Int, word: String) {
        val wordView: WordView = findViewWithTag("${column}x$row")
        wordView.text = word
    }

    class WordGrid(
        val wordLineCount: Int,
        private val getBoardLineSize: () -> Int
    ) {
        val wordCount = wordLineCount * wordLineCount
        val boardLineSize get() = getBoardLineSize()
        val wordSize : Float get() = boardLineSize.toFloat() / wordLineCount

        fun getWordPoint(point: PointF): WordPoint {
            val row = floor(point.y / wordSize).toInt() + 1
            val column = floor(point.x / wordSize).toInt() + 1

            Timber.i("word $column x $row")

            return getWordPoint(column, row)
        }

        fun getWordPoint(column: Int, row: Int) = WordPoint(column, row)

        inner class WordPoint(
            val column: Int,
            val row: Int
        ) {
            val end = (wordSize * column)
            val bottom = (wordSize * row)
            val top = bottom - wordSize
            val start = end - wordSize

            val center = PointF(
                end - wordSize / 2f,
                bottom - wordSize / 2f
            )
        }
    }
}