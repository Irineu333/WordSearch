package com.neo.wordsearch

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.setPadding
import timber.log.Timber

class WordView(
    context: Context,
    attr: AttributeSet? = null
) : AppCompatTextView(context, attr) {

    init {
        text = "A"
        gravity = Gravity.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

        Timber.i("width $width")

        setTextSize(TypedValue.COMPLEX_UNIT_PX, measuredWidth * 0.75f)
    }

}