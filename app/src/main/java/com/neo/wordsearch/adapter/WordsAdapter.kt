package com.neo.wordsearch

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neo.wordsearch.databinding.ItemWordBinding
import com.neo.wordsearch.model.WordModel

class WordsAdapter(
    private val getWords: () -> Array<WordModel>
) : RecyclerView.Adapter<WordsHolder>() {

    private val words get() = getWords()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordsHolder {
        return WordsHolder(
            ItemWordBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: WordsHolder, position: Int) {
        val word = words[position]

        holder.bind(word)
    }

    override fun getItemCount() = words.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateAll() {
        notifyDataSetChanged()
    }
}

class WordsHolder(
    private val binding: ItemWordBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(word: WordModel) {
        binding.cvContainer.setCardBackgroundColor(word.color)

        binding.tvWord.text = word.text

        if (word.selected) {
            binding.tvWord.text = SpannableString(word.text).apply {
                setSpan(
                    StrikethroughSpan(),
                    0, length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        } else {
            binding.tvWord.text = word.text
        }
    }
}