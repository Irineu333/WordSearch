package com.neo.wordsearch.model

import java.io.Serializable

class Game(
    val puzzle : Array<Array<Char>>,
    val words : Array<String>
) : Serializable