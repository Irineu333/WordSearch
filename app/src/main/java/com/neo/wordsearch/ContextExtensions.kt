package com.neo.wordsearch

import android.content.Context

fun Context.dp(dp: Int) = PxUtils(this.resources.getDimension(R.dimen.dimen_1dp) * dp)
fun Context.dp(dp: Float) = PxUtils(this.resources.getDimension(R.dimen.dimen_1dp) * dp)

class PxUtils(val px: Int) {
    constructor(px: Float) : this(px.toInt())
}
