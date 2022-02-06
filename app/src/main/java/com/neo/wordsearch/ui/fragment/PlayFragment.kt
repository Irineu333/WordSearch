package com.neo.wordsearch.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.neo.wordsearch.databinding.FragmentPlayBinding
import com.neo.wordsearch.model.Difficulty
import com.neo.wordsearch.model.Game
import com.neo.wordsearch.model.Puzzle
import com.neo.wordsearch.ui.activity.game.GameActivity
import timber.log.Timber

class PlayFragment : Fragment() {

    private lateinit var binding: FragmentPlayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPlayBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        setupSize()
        setupDifficulty()
        setupListeners()
    }

    private val words = arrayOf(
        "Irineu",
        "Gabriel",
        "Estefany",
        "João",
        "Gilson",
        "Tatiane",
        "Lucília",
        "Estefany",
        "Caroline",
        "Aninha",
        "Giovanna",
        "Paulo",
        "Carlos",
        "Kleber",
        "Lula",
        "Aderson",
        "Nino",
        "Sandro",
        "Joaquina",
        "Cirilo",
        "Galega",
        "Joana",
        "Jessica",
        "Orochinho",
        "Lucas",
        "Cachorro",
        "Gato",
    )

    private fun setupListeners() {
        binding.buttonPlay.setOnClickListener {
            val size = binding.containerSize.seek.progress + 10
            val wordsCount = getWordsCount(size)
            val difficulty = getDifficulty()

            this.words.shuffle()

            val words = this.words.copyOfRange(0, wordsCount)

            val puzzle =
                Puzzle(size, size).create(
                    words,
                    difficulty.orientations
                )

            requireActivity()
                .startActivity(
                    Intent(requireContext(), GameActivity::class.java).apply {
                        putExtra(
                            "puzzle", Game(
                                puzzle = puzzle,
                                words = words
                            )
                        )
                    }
                )
        }
    }

    private fun setupSize() {
        binding.containerSize.tvLeft.text = "10x10"
        binding.containerSize.tvCenter.text = "15x15"
        binding.containerSize.tvRight.text = "20x20"

        binding.containerSize.seek.max = 10

        binding.containerSize.seek.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    setSizeProgress(progress + 10)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
            }
        )

        setSizeProgress(10)
    }

    private fun setupDifficulty() {
        binding.containerDifficulty.tvLeft.text = "Fácil"
        binding.containerDifficulty.tvCenter.text = "Médio"
        binding.containerDifficulty.tvRight.text = "Difícil"

        binding.containerDifficulty.seek.max = 2

        binding.containerDifficulty.seek.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    setDifficultyProgress(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
            }
        )

        setDifficultyProgress(0)
    }


    private fun setDifficultyProgress(progress: Int) {
        val difficulty = getDifficulty(progress)

        binding.tvOptionsDifficulty.text = "vertical: ${difficulty.vertical}\n" +
                "horizontal: ${difficulty.horizontal}\n" +
                "diagonal: ${difficulty.diagonal}"
    }

    private fun setSizeProgress(progress: Int) {

        Timber.i("progress $progress")

        val words = getWordsCount(progress)

        binding.tvOptionSize.text = "tamanho: ${progress}x$progress\npalavras: $words"
    }

    private fun getDifficulty(
        progress: Int = binding.containerDifficulty.seek.progress
    ): Difficulty {
        return when (progress) {
            0 -> Difficulty.Easy()

            1 -> Difficulty.Medium()

            2 -> Difficulty.Hard()

            else -> throw RuntimeException()
        }
    }

    private fun getWordsCount(size: Int) = when {
        size == 20 -> 30
        size >= 15 -> 15
        size >= 10 -> 10
        else -> {
            throw RuntimeException()
        }
    }
}