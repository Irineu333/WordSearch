package com.neo.wordsearch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.setPadding
import timber.log.Timber
import kotlin.math.floor
import kotlin.math.max

class WordBoard(
    context: Context, attr: AttributeSet? = null
) : LinearLayout(context, attr), OnTouchEvent {

    private var wordLineCount = 0
    private var boardLineSize = 0

    private lateinit var grid: WordGrid

    private val wordSize get() = boardLineSize / wordLineCount

    private var actualLine: Pair<PointF, PointF>? = null

    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = context.dp(28f).px
        strokeCap = Paint.Cap.ROUND
    }

    init {
        orientation = VERTICAL
        setPadding(context.dp(2).px)

        setOnTouchListener(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

        boardLineSize = max(measuredWidth, measuredHeight)

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

        Timber.i("words size -> $wordSize")
        Timber.i("board size -> $boardLineSize")

        for (view in children) {
            view.layoutParams.apply {

                width = boardLineSize
                height = wordSize

                requestLayout()
            }
            for (word in (view as ViewGroup).children) {
                val wordView = word as WordView
                wordView.layoutParams.apply {

                    width = wordSize
                    height = wordSize

                    requestLayout()
                }
            }
        }
    }

    private fun createPuzzle(wordLineCount: Int) {

        if (wordLineCount <= 0) throw IllegalArgumentException("length not be 0 or negative")

        this.wordLineCount = wordLineCount

        Timber.i("create puzzle $wordLineCount x $wordLineCount")

        grid = WordGrid(
            wordLineCount = wordLineCount,
            getBoardLineSize = { boardLineSize },
            getWordSize = { wordSize },
            getPadding = { paddingTop or paddingStart }
        )

        removeAllViews()

        for (i in 0 until wordLineCount) {
            val container = LinearLayout(context).apply {

                orientation = HORIZONTAL
                gravity = Gravity.CENTER

                for (j in 0 until wordLineCount) {
                    addView(WordView(context), j)
                }

            }
            addView(container, i)
        }

        adjustWordsSize()
    }

    fun renderPuzzle(words: Array<Array<String>>) {

        if (words.any { it.size != words.size })
            throw IllegalArgumentException("horizontal and vertical length must be equals")

        if (words.size != wordLineCount) {
            createPuzzle(words.size)
        }

        words.forEachIndexed { h, it ->
            it.forEachIndexed { v, word ->
                setText(h, v, word)
            }
        }
    }

    private fun setText(h: Int, v: Int, word: String) {
        ((getChildAt(h) as ViewGroup)[v] as WordView).text = word
    }

    override fun down(event: MotionEvent) {
        val wordPoints = grid.getWordPoints(event.x, event.y)
        val singlePoint = wordPoints.center
        actualLine = singlePoint to singlePoint

        invalidate()
    }

    override fun up(event: MotionEvent) {
        actualLine = null

        invalidate()
    }

    override fun move(event: MotionEvent) {
        val wordPoints = grid.getWordPoints(event.x, event.y)
        actualLine = actualLine?.copy(second = wordPoints.center)

        invalidate()
    }

    class WordGrid(
        val wordLineCount: Int,
        private val getBoardLineSize: () -> Int,
        private val getWordSize: () -> Int,
        private val getPadding: () -> Int
    ) {
        val wordCount = wordLineCount * wordLineCount
        val wordSize get() = getWordSize()
        val boardLineSize get() = getBoardLineSize()

        fun getWordPoints(x: Float, y: Float): WordPoints {
            val row = floor(y / wordSize).toInt() + 1
            val column = floor(x / wordSize).toInt() + 1

            Timber.i("word $column x $row")
            return WordPoints(row, column)
        }

        inner class WordPoints(
            val row: Int,
            val column: Int
        ) {
            val end = wordSize * column + getPadding()
            val bottom = wordSize * row + getPadding()
            val top = bottom - wordSize
            val start = end - wordSize
            val center = PointF(end - wordSize / 2f, bottom - wordSize / 2f)
        }
    }
}