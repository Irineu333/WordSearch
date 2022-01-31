package com.neo.wordsearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.neo.wordsearch.databinding.ActivityMainBinding
import timber.log.Timber

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val wordsAdapter by setupWordsAdapter()

    private var words: Array<String> = arrayOf()

    private fun setupWordsAdapter() = lazy {
        WordsAdapter(
            getWords = {
                words
            }
        ).apply {
            binding.rvWords.adapter = this
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        Timber.i("init")
        setupWordBoard()
        setupLetterBoard()
    }

    private fun setupWordBoard() {
        words = arrayOf(
            "IRINEU",
            "ESTEFANY",
            "GABRIEL"
        )

        wordsAdapter.updateAll()
    }

    private fun setupLetterBoard() {
        binding.latterBoard.renderPuzzle(
            arrayOf(
                arrayOf("A", "B", "C", "D"),
                arrayOf("E", "F", "G", "H"),
                arrayOf("I", "J", "K", "L"),
                arrayOf("M", "N", "O", "P"),
            )
        )
    }
}