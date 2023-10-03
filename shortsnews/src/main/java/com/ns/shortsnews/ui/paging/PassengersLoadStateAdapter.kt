package com.ns.shortsnews.ui.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ns.shortsnews.databinding.ItemLoadingStateBinding

class PassengersLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<PassengersLoadStateAdapter.PassengerLoadStateViewHolder>() {

    inner class PassengerLoadStateViewHolder(
        private val binding: ItemLoadingStateBinding,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.textViewError.text = loadState.error.localizedMessage
            }
            binding.progressbar.visible(loadState is LoadState.Loading)
            binding.buttonRetry.visible(loadState is LoadState.Error)
            binding.textViewError.visible(loadState is LoadState.Error)
            binding.buttonRetry.setOnClickListener {
                retry()
            }
        }
    }

    override fun onBindViewHolder(holder: PassengerLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ) = PassengerLoadStateViewHolder(
        ItemLoadingStateBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        retry
    )
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}