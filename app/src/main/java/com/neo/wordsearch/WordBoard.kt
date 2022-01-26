package com.neo.wordsearch

import android.content.Context
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

    private var length = 0
    private var size = 0

    init {
        orientation = VERTICAL
        setPadding(context.dp(2).px)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

        size = max(measuredWidth, measuredHeight)

        adjustWordsSize()
    }

    private fun adjustWordsSize() {
        val wordSize = size / length

        Timber.i("adjustWordsSize -> $wordSize")

        for (view in children) {
            view.layoutParams.apply {

                width = measuredWidth
                height = wordSize

                view.layoutParams = this
            }
            for (word in (view as ViewGroup).children) {
                word.layoutParams.apply {

                    width = wordSize
                    height = wordSize

                    word.layoutParams = this
                }
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

        if (puzzle.size != length) {
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