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
    private val boardLineSize get() = measuredHeight

    private lateinit var grid: WordGrid

    private var actualLine: Pair<PointF, PointF>? = null

    private lateinit var paint : Paint

    init {
        orientation = VERTICAL
        setPadding(0)

        setOnTouchListener(this)
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

        for (view in children) {
            view.layoutParams.apply {

                width = boardLineSize
                height = grid.wordSize
                setPadding(0)

                requestLayout()
            }
            for (word in (view as ViewGroup).children) {
                val wordView = word as WordView
                wordView.layoutParams.apply {

                    width = grid.wordSize
                    height = grid.wordSize

                    requestLayout()
                }
            }
        }

        paint = Paint().apply {
            color = Color.RED
            strokeWidth = grid.wordSize * 0.75f
            strokeCap = Paint.Cap.ROUND
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

        for (i in 0 until wordLineCount) {
            val container = LinearLayout(context).apply {

                orientation = HORIZONTAL
                gravity = Gravity.START

                for (j in 0 until wordLineCount) {
                    addView(WordView(context), j)
                }
            }
            addView(container, i)
        }

        adjustWordsSize()
    }

    fun renderPuzzle(words: Array<Array<String>>) {

        Timber.i("create puzzle $words x $words")

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

        actualLine = wordPoints.center.let { it to it }

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
        private val getBoardLineSize: () -> Int
    ) {
        val wordCount = wordLineCount * wordLineCount
        val boardLineSize get() = getBoardLineSize()
        val wordSize get() = boardLineSize / wordLineCount

        fun getWordPoints(x: Float, y: Float): WordPoint {
            val row = floor(y / wordSize).toInt() + 1
            val column = floor(x / wordSize).toInt() + 1

            Timber.i("word $column x $row")

            return WordPoint(row, column)
        }

        inner class WordPoint(
            val row: Int,
            val column: Int
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