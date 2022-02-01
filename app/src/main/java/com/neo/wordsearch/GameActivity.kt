package com.neo.wordsearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.neo.wordsearch.databinding.ActivityMainBinding
import timber.log.Timber

class GameActivity : AppCompatActivity(), OnSelectListener {

    private lateinit var binding: ActivityMainBinding

    private val wordsAdapter by setupWordsAdapter()

    private var words: Array<WordModel> = arrayOf()

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
            WordModel("IRINEU"),
            WordModel("TEFY"),
            WordModel("GABRIEL")
        )

        wordsAdapter.updateAll()
    }

    private fun setupLetterBoard() {
        binding.latterBoard.renderPuzzle(
            arrayOf(
                arrayOf("I", "R", "I", "N", "E", "U", "O"),
                arrayOf("E", "E", "G", "H", "A", "M", "O"),
                arrayOf("I", "J", "S", "T", "A", "F", "Y"),
                arrayOf("M", "N", "O", "P", "K", "T", "A"),
                arrayOf("G", "A", "B", "R", "I", "E", "L"),
                arrayOf("M", "N", "O", "P", "K", "F", "K"),
                arrayOf("E", "S", "T", "E", "K", "Y", "K"),
            )
        )

        binding.latterBoard.onSelectListener = this
    }

    override fun selectWord(word: String, color: Int): Boolean {
        binding.tvNewWord.text = word
        binding.tvNewWord.setTextColor(color)

        for ((i, it) in words.withIndex()) {
            if (it.text == word) {
                words[i] = it.copy(color = color, selected = true)
                wordsAdapter.updateAll()
                return true
            }
        }

        return false
    }
}