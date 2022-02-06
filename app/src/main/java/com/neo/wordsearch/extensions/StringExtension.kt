package com.neo.wordsearch.extensions

fun String.inverted(): String {

    val string = StringBuilder()

    for (i in length - 1 downTo 0) {
        string.append(this[i])
    }

    return string.toString()
}