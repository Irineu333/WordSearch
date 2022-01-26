package com.neo.wordsearch

import android.content.Context
import androidx.annotation.DimenRes

fun Context.dp(dp: Int) =
    IntPxUtils(this.requireDimen(R.dimen.dimen_1dp).toInt() * dp)

fun Context.dp(dp: Float) =
    FloatPxUtils(this.requireDimen(R.dimen.dimen_1dp) * dp)

class IntPxUtils(val px: Int)
class FloatPxUtils(val px: Float)

fun Context.requireDimen(@DimenRes dimenResId: Int) =
    this.resources.getDimension(dimenResId)