package com.neo.wordsearch

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.setPadding

class GameView(
    context: Context, attr: AttributeSet? = null
) : LinearLayout(context, attr) {

    private var length = 0

    init {
        orientation = VERTICAL
        setPadding(context.dp(2).px)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

        adjust()
    }

    private fun adjust() {
        for (view in children) {
            view.layoutParams = LayoutParams(
                measuredWidth, measuredHeight / length
            )
            for (text in (view as ViewGroup).children) {
                text.layoutParams = LayoutParams(
                    measuredWidth / length, measuredHeight / length
                )
            }
        }
    }

    private fun createPuzzle(length: Int) {
        this.length = length
        removeAllViews()

        for (i in 0 until length) {
            val container = LinearLayout(context).apply {

                orientation = HORIZONTAL
                gravity = Gravity.CENTER

            }

            for (j in 0 until length) {
                container.addView(
                    WordView(context), j
                )
            }

            addView(container, i)
        }

        invalidate()
    }

    fun renderPuzzle(puzzle: Array<Array<String>>) {

        if (puzzle.any { it.size != puzzle.size })
            throw IllegalArgumentException("horizontal and vertical length must be equals")

        if (puzzle.size != length) {
            createPuzzle(puzzle.size)
        }

        puzzle.forEachIndexed { i, it ->
            it.forEachIndexed { j, word ->
                setText(i, j, word)
            }
        }
    }

    private fun setText(i: Int, j: Int, word: String) {
        ((getChildAt(i) as ViewGroup)[j] as WordView).text = word
    }
}