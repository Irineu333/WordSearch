package com.neo.wordsearch.model

import com.neo.wordsearch.extensions.inverted
import kotlin.random.Random

class Puzzle(
    private val rows: Int,
    private val columns: Int,
) {

    fun create(
        words: Array<String>,
        orientations: Array<Orientation>,
        fill: Boolean = true
    ): Array<Array<Char>> {

        //check words size
        if (words.isEmpty()) {
            throw IllegalStateException("words cannot be empty")
        }

        //check word size
        if (words.any { it.length > rows || it.length > columns }) {
            throw IllegalStateException("words cannot be larger than columns or rows")
        }

        //check orientation empty
        if (orientations.isEmpty()) {
            throw IllegalStateException("mandatory to inform an orientation")
        }

        //check orientation probability
        if (orientations.map { it.probability }
                .reduce { acc, orientation -> acc + orientation } != 100) {
            throw IllegalStateException("the sum of the probabilities of giving 100")
        }

        //create puzzle
        val tempPuzzle = Array(rows) { Array<Char?>(columns) { null } }

        var success = false
        var attempt = 0

        do {
            tempPuzzle.clear()

            for (word in words.apply { shuffle() }) {
                success = tempPuzzle.insertWord(orientations, word)

                if (!success) break
            }

        } while (!success && ++attempt < 100)

        if (!success) {
            throw NoSolvedException("couldn't solve")
        }

        if (fill)
            tempPuzzle.fill()

        return Array(rows) { row -> Array(columns) { column -> tempPuzzle[row][column] ?: '-' } }
    }

    private fun Array<Array<Char?>>.insertWord(
        orientations: Array<Orientation>,
        word: String
    ): Boolean {

        var success: Boolean
        var attempt = 0

        do {
            val orientation = orientations.randomOrientation()

            val mWord = if (orientation.randomInverted())
                word.inverted() else word

            success = when (orientation) {
                is Orientation.Vertical -> {
                    this.insertVertical(mWord.uppercase())
                }

                is Orientation.Horizontal -> {
                    this.insertHorizontal(mWord.uppercase())
                }

                is Orientation.Diagonal -> {
                    this.insertDiagonal(mWord.uppercase())
                }
            }
        } while (!success && ++attempt <= 100)

        return success
    }

    private fun Array<Array<Char?>>.insertVertical(word: String): Boolean {
        val lastLetter = word.length - 1

        val wordRage = 0..lastLetter

        var startRow: Int
        var column: Int

        var success: Boolean
        var attempt = 0

        do {
            startRow = Random.nextInt(0, rows - lastLetter)
            column = Random.nextInt(0, columns)

            success = true
            for (i in wordRage) {
                val it = this[startRow + i][column]

                if (it != null && it != word[i]) {
                    success = false
                    break
                }
            }

        } while (!success && ++attempt <= rows * columns)

        if (success) {
            //insert
            for (i in wordRage) {

                val it = this[startRow + i][column]

                if (it != null && it != word[i]) throw RuntimeException()

                this[startRow + i][column] = word[i]
            }
        }

        return success
    }

    private fun Array<Array<Char?>>.insertHorizontal(word: String): Boolean {
        val lastLetter = word.length - 1

        val wordRage = 0..lastLetter

        var row: Int
        var startColumn: Int

        var success: Boolean
        var attempt = 0

        do {
            row = Random.nextInt(0, rows)
            startColumn = Random.nextInt(0, columns - lastLetter)

            success = true
            for (i in wordRage) {
                val it = this[row][startColumn + i]

                if (it != null && it != word[i]) {
                    success = false
                    break
                }
            }

        } while (!success && ++attempt <= rows * columns)

        if (success) {
            //insert
            for (i in wordRage) {

                val it = this[row][startColumn + i]

                if (it != null && it != word[i]) throw RuntimeException()

                this[row][startColumn + i] = word[i]
            }
        }

        return success
    }

    private fun Array<Array<Char?>>.insertDiagonal(word: String): Boolean {
        val lastLetter = word.length - 1

        val wordRage = 0..lastLetter

        var startRow: Int
        var startColumn: Int

        var success: Boolean
        var attempt = 0

        do {

            startColumn = Random.nextInt(0, columns - lastLetter)

            success = true

            if (Random.nextBoolean()) {

                startRow = Random.nextInt(0, rows - lastLetter)

                for (i in wordRage) {
                    val it = this[startRow + i][startColumn + i]

                    if (it != null && it != word[i]) {
                        success = false
                        break
                    }
                }

                if (success) {
                    //insert
                    for (i in wordRage) {

                        val it = this[startRow + i][startColumn + i]

                        if (it != null && it != word[i]) throw RuntimeException()

                        this[startRow + i][startColumn + i] = word[i]
                    }
                }
            } else {
                startRow = Random.nextInt(lastLetter, rows)

                for (i in wordRage) {
                    val it = this[startRow - i][startColumn + i]

                    if (it != null && it != word[i]) {
                        success = false

                        break
                    }
                }

                if (success) {
                    //insert
                    for (i in wordRage) {

                        val it = this[startRow - i][startColumn + i]

                        if (it != null && it != word[i]) throw RuntimeException()

                        this[startRow - i][startColumn + i] = word[i]
                    }
                }
            }

        } while (!success && ++attempt <= (rows * columns) * 2)

        return success
    }

    private fun Array<Orientation>.randomOrientation(): Orientation {

        var maxProbability = 0

        val probabilities = IntArray(this.size) { 0 }

        for ((index, it) in this.withIndex()) {
            maxProbability += it.probability
            probabilities[index] = maxProbability
        }

        val randomOrientation = Random.nextInt(1, maxProbability)

        for ((index, orientation) in this.withIndex()) {
            if (probabilities[index] > randomOrientation) {
                return orientation
            }
        }

        throw RuntimeException()
    }

    private fun randomLetter(): Char {

        return when (Random.nextInt(1, 26)) {
            1 -> 'A'
            2 -> 'B'
            3 -> 'C'
            4 -> 'D'
            5 -> 'E'
            6 -> 'F'
            7 -> 'G'
            8 -> 'H'
            9 -> 'I'
            10 -> 'J'
            11 -> 'K'
            12 -> 'L'
            13 -> 'M'
            14 -> 'N'
            15 -> 'O'
            16 -> 'P'
            17 -> 'Q'
            18 -> 'R'
            19 -> 'S'
            20 -> 'T'
            21 -> 'U'
            22 -> 'V'
            23 -> 'W'
            24 -> 'X'
            25 -> 'Y'
            26 -> 'Z'
            else -> throw RuntimeException()
        }
    }

    private fun Orientation.randomInverted(): Boolean {
        return invertedProbability >= Random.nextInt(0, 101)
    }

    private fun Array<Array<Char?>>.clear() {
        for (i in this.indices) {
            for (j in this[i].indices) {
                this[i][j] = null
            }
        }
    }

    private fun Array<Array<Char?>>.fill() {
        for (row in this.indices) {
            for (column in this[row].indices) {
                if (this[row][column] == null) {
                    this[row][column] = randomLetter()
                }
            }
        }
    }

    class NoSolvedException(message: String) : Exception(message)
}

sealed class Orientation(
    val probability: Int,
    val invertedProbability: Int
) {
    open val tag: String get() = throw RuntimeException()

    override fun toString(): String {
        return tag
    }

    class Vertical(
        probability: Int, inverted: Int = 0
    ) : Orientation(
        probability, inverted
    ) {
        override val tag = "Vertical"
    }

    class Horizontal(
        probability: Int, inverted: Int = 0
    ) : Orientation(
        probability, inverted
    ) {
        override val tag = "Horizontal"
    }

    class Diagonal(
        probability: Int, inverted: Int = 0
    ) : Orientation(
        probability, inverted
    ) {
        override val tag = "Diagonal"
    }
}