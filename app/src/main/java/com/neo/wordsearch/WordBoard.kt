package com.neo.wordsearch

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.setPadding
import timber.log.Timber
import kotlin.math.max

class WordBoard(
    context: Context, attr: AttributeSet? = null
) : LinearLayout(context, attr) {

    private var wordLength = 0
    private var boardSizePx = 0

    private val wordsSizePx get() = boardSizePx / wordLength

    init {
        orientation = VERTICAL
        setPadding(context.dp(2).px)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

        boardSizePx = max(measuredWidth, measuredHeight)

        adjustWordsSize()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    private fun adjustWordsSize() {

        if (wordLength == 0) return

        Timber.i("words size -> $wordsSizePx")
        Timber.i("board size -> $boardSizePx")

        for (view in children) {
            view.layoutParams.apply {

                width = boardSizePx
                height = wordsSizePx

                requestLayout()
            }
            for (word in (view as ViewGroup).children) {
                val wordView = word as WordView
                wordView.layoutParams.apply {

                    width = wordsSizePx
                    height = wordsSizePx

                    requestLayout()
                }
            }
        }
    }

    private fun createPuzzle(length: Int) {

        if (length <= 0) throw IllegalArgumentException("length not be 0 or negative")

        this.wordLength = length

        Timber.i("word length -> $length")

        removeAllViews()

        for (i in 0 until length) {
            val container = LinearLayout(context).apply {

                orientation = HORIZONTAL
                gravity = Gravity.CENTER

                for (j in 0 until length) {
                    addView(WordView(context), j)
                }

            }
            addView(container, i)
        }

        adjustWordsSize()
    }

    fun renderPuzzle(puzzle: Array<Array<String>>) {

        if (puzzle.any { it.size != puzzle.size })
            throw IllegalArgumentException("horizontal and vertical length must be equals")

        if (puzzle.size != wordLength) {
            createPuzzle(puzzle.size)
        }

        puzzle.forEachIndexed { h, it ->
            it.forEachIndexed { v, word ->
                setText(h, v, word)
            }
        }
    }

    private fun setText(h: Int, v: Int, word: String) {
        ((getChildAt(h) as ViewGroup)[v] as WordView).text = word
    }
}