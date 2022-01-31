package com.neo.wordsearch

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neo.wordsearch.databinding.ItemWordBinding

class WordsAdapter(
    private val getWords: () -> Array<String>
) : RecyclerView.Adapter<WordsHolder>() {

    private val words get() = getWords()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordsHolder {
        return WordsHolder(ItemWordBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: WordsHolder, position: Int) {
        val word = words[position]

        holder.setWord(word)
    }

    override fun getItemCount() = words.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateAll() {
        notifyDataSetChanged()
    }
}

class WordsHolder(
    private val binding: ItemWordBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val context get() = itemView.context

    fun setWord(word: String) {
        binding.tvWord.text = word
    }
}