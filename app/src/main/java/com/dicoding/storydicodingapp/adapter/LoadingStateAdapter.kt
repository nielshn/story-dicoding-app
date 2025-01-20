package com.dicoding.storydicodingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.storydicodingapp.databinding.ItemStoryBinding

class LoadingStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {

    inner class LoadingStateViewHolder(private val binding: ItemStoryBinding, private val retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(loadState: LoadState){
                if (loadState is LoadState.Error){
                    binding.errorText.visibility = View.VISIBLE
                    binding.retryButton.visibility = View.VISIBLE

                    binding.retryButton.setOnClickListener {
                        retry()
                    }
                }else {
                    binding.errorText.visibility = View.GONE
                    binding.retryButton.visibility = View.GONE
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingStateViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}
