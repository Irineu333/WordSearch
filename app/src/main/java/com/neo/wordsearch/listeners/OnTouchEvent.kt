package com.neo.wordsearch.listeners

import android.view.MotionEvent
import android.view.View
import timber.log.Timber

interface OnTouchEvent : View.OnTouchListener {
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                Timber.i("down")
                down(event)
                true
            }

            MotionEvent.ACTION_UP -> {
                Timber.i("up")
                up(event)
                false
            }

            MotionEvent.ACTION_MOVE -> {
                Timber.i("move")
                move(event)
                true
            }

            else -> false
        }
    }

    abstract fun down(event: MotionEvent)

    abstract fun up(event: MotionEvent)

    abstract fun move(event: MotionEvent)
}
