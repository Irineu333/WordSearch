package com.neo.wordsearch.ui.game

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
import com.neo.wordsearch.databinding.ActivityMainBinding
import com.neo.wordsearch.model.WordModel
import timber.log.Timber

class GameActivity : AppCompatActivity(), OnSelectListener {

    private lateinit var binding: ActivityMainBinding

    private val wordsAdapter by setupWordsAdapter()

    private var words: Array<WordModel> = arrayOf()

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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {

        Timber.i("init")
        setupWordBoard()
        setupLetterBoard()
        updateProgress()
    }

    private fun setupWordBoard() {
        words = arrayOf(
            WordModel("IRINEU"),
            WordModel("TEFY"),
            WordModel("GABRIEL"),
            WordModel("JOAO"),
            WordModel("TATI"),
            WordModel("ANE"),
            WordModel("TEST1"),
            WordModel("TEST2"),
            WordModel("TEST3"),
            WordModel("TEST4"),
            WordModel("TEST5"),
            WordModel("TEST6"),
            WordModel("TEST7"),
        )

        wordsAdapter.updateAll()

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

        selection(null, null)
    }

    private fun setupLetterBoard() {
        binding.containerLetterBoard.latterBoard.renderPuzzle(
            arrayOf(
                arrayOf("I", "R", "I", "N", "E", "U", "O"),
                arrayOf("E", "S", "G", "T", "L", "M", "O"),
                arrayOf("I", "J", "E", "N", "A", "F", "Y"),
                arrayOf("M", "O", "O", "P", "K", "T", "A"),
                arrayOf("G", "A", "B", "R", "I", "E", "L"),
                arrayOf("M", "O", "O", "P", "K", "F", "K"),
                arrayOf("E", "S", "T", "E", "K", "Y", "K"),
            )
        )

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
            if (!it.selected && it.text == word) {
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