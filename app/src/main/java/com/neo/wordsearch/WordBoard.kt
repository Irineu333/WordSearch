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

    private var wordSize = 0
    private var boardSize = 0

    init {
        orientation = VERTICAL
        setPadding(context.dp(2).px)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

        boardSize = max(measuredWidth, measuredHeight)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        adjustWordsSize()
    }

    private fun adjustWordsSize() {

        if (wordSize == 0) return

        val wordsSize = boardSize / wordSize

        Timber.i("words size -> $wordsSize")
        Timber.i("board size -> $boardSize")

        for (view in children) {
            view.layoutParams.apply {

                width = boardSize
                height = wordsSize

                requestLayout()
            }
            for (word in (view as ViewGroup).children) {
                val wordView = word as WordView
                wordView.layoutParams.apply {

                    width = wordsSize
                    height = wordsSize

                    requestLayout()
                }
            }
        }
    }

    private fun createPuzzle(length: Int) {
        this.wordSize = length
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

        if (puzzle.size != wordSize) {
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