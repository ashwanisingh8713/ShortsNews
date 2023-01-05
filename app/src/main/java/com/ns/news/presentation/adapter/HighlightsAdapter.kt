package com.ns.news.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ns.news.R
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.db.Cell
import com.ns.news.databinding.ArticleHighlightsBinding
import com.ns.news.databinding.NotDefinedBinding
import com.ns.news.databinding.WidgetVtTopNewsCorousalItemBinding
import com.ns.news.domain.model.ViewType

class HighlightsAdapter(private val highlights: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ArticleHighlightsVH(
            ArticleHighlightsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ArticleHighlightsVH).bind(highlights[position], position)
    }

    override fun getItemCount(): Int {
        return highlights.size
    }




}

class ArticleHighlightsVH(
    private val binding: ArticleHighlightsBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(highlight: String, index: Int) {
        binding.apply {
            titleTv.text = highlight
        }
    }
}