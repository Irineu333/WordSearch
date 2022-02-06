package com.neo.wordsearch.ui.activity.game

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxItemDecoration
import com.neo.wordsearch.OnSelectListener
import com.neo.wordsearch.WordsAdapter
import com.neo.wordsearch.databinding.ActivityGameBinding
import com.neo.wordsearch.model.Game
import com.neo.wordsearch.model.WordModel

class GameActivity : AppCompatActivity(), OnSelectListener {

    private lateinit var binding: ActivityGameBinding

    private val wordsAdapter by setupWordsAdapter()

    private lateinit var words: Array<WordModel>
    private lateinit var puzzle: Array<Array<Char>>

    private fun setupWordsAdapter() = lazy {
        WordsAdapter(
            getWords = {
                words
            },
        ).apply {
            binding.containerWords.rvWords.adapter = this
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {

        setupArguments()
        setupWordBoard()
        setupLetterBoard()
        updateProgress()
    }

    private fun setupArguments() {
        val game = intent!!.getSerializableExtra("puzzle") as Game

        words = game.words.map { WordModel(text = it) }.toTypedArray()
        puzzle = game.puzzle
    }

    private fun setupWordBoard() {

        binding.containerWords.rvWords.addItemDecoration(
            object : FlexboxItemDecoration(this) {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {

                    outRect.right += 100

                    super.getItemOffsets(outRect, view, parent, state)
                }
            }.apply {
                setOrientation(FlexboxItemDecoration.VERTICAL)
            }
        )

        wordsAdapter.updateAll()

        selection(null, null)
    }

    private fun setupLetterBoard() {
        binding.containerLetterBoard.latterBoard.renderPuzzle(puzzle)
        binding.containerLetterBoard.latterBoard.onSelectListener = this
    }

    override fun selection(word: String?, color: Int?): Boolean {
        binding.tvSelection.text = word ?: "---"

        if (color == null) {
            binding.tvSelection.setTextColor(Color.DKGRAY)
            return false
        }

        binding.tvSelection.setTextColor(color)

        for ((i, it) in words.withIndex()) {
            if (!it.selected && it.text.uppercase() == word?.uppercase()) {
                words[i] = it.copy(color = color, selected = true)
                wordsAdapter.updateAll()
                updateProgress()
                return true
            }
        }

        return false
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgress() {
        val all = words.count()
        val selected = words.count { it.selected }

        binding.tvProgress.text = "$selected/$all"
    }
}