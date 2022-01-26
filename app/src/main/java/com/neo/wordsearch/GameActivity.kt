package com.neo.wordsearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.neo.wordsearch.databinding.ActivityMainBinding
import timber.log.Timber
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        Timber.i("init")

        binding.gameView.renderPuzzle(randomPuzzle(5, 5))

        binding.n5x5.setOnClickListener {
            binding.gameView.renderPuzzle(randomPuzzle(5, 5))
        }

        binding.n10x10.setOnClickListener {
            binding.gameView.renderPuzzle(randomPuzzle(10, 10))
        }

        binding.n15x15.setOnClickListener {
            binding.gameView.renderPuzzle(randomPuzzle(15, 15))
        }
        binding.n20x20.setOnClickListener {
            binding.gameView.renderPuzzle(randomPuzzle(20, 20))
        }
    }

    private fun randomPuzzle(z: Int, v: Int): Array<Array<String>> {
        val array = Array(z) { Array(v) { "A" } }

        array.forEach {
            for (index in it.indices) {
                it[index] = randomWord()
            }
        }

        return array
    }

    private fun randomWord() = when (Random.nextInt(1, 26)) {
        1 -> "A"
        2 -> "B"
        3 -> "C"
        4 -> "D"
        5 -> "E"
        6 -> "F"
        7 -> "G"
        8 -> "H"
        9 -> "I"
        10 -> "J"
        11 -> "K"
        12 -> "L"
        13 -> "M"
        14 -> "N"
        15 -> "O"
        16 -> "P"
        17 -> "Q"
        18 -> "R"
        19 -> "S"
        20 -> "T"
        21 -> "U"
        22 -> "V"
        23 -> "W"
        24 -> "X"
        25 -> "Y"
        26 -> "Z"
        else -> throw IllegalArgumentException()
    }
}