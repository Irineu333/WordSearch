package com.neo.wordsearch

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import timber.log.Timber

@SuppressLint("AppCompatCustomView")
class WordView(
    context: Context,
    attr: AttributeSet? = null
) : TextView(context, attr) {

    private var textPercentSize = 0.80f

    init {
        text = "A"
        gravity = Gravity.CENTER
        includeFontPadding = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

        textSize = measuredWidth * textPercentSize

        Timber.i("view size $measuredWidth")
        Timber.i("text size $textSize")
    }

    override fun setTextSize(size: Float) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

}