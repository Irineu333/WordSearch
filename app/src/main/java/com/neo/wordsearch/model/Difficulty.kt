package com.neo.wordsearch.model

sealed class Difficulty(
    val orientations: Array<Orientation>
) {
    val vertical: Boolean get() = orientations.any { it is Orientation.Vertical }
    val horizontal: Boolean get() = orientations.any { it is Orientation.Horizontal }
    val diagonal: Boolean get() = orientations.any { it is Orientation.Diagonal }

    class Easy : Difficulty(
        arrayOf(
            Orientation.Vertical(50),
            Orientation.Horizontal(50),
        )
    )

    class Medium: Difficulty(
        arrayOf(
            Orientation.Vertical(35),
            Orientation.Horizontal(35),
            Orientation.Diagonal(30)
        )
    )

    class Hard: Difficulty(
        arrayOf(
            Orientation.Vertical(35, 50),
            Orientation.Horizontal(35, 50),
            Orientation.Diagonal(30, 20)
        )
    )
}