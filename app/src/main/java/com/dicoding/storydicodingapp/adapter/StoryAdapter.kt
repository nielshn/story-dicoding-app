package com.dicoding.storydicodingapp.adapter

import android.app.Activity
import android.content.Intent
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.storydicodingapp.R
import com.dicoding.storydicodingapp.data.api.response.ListStoryItem
import com.dicoding.storydicodingapp.databinding.ItemStoryBinding
import com.dicoding.storydicodingapp.ui.detail.DetailStoryActivity
import com.dicoding.storydicodingapp.utils.dateFormatter

class StoryAdapter :
    PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(story.photoUrl).apply(
                        RequestOptions.placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                    ).into(ivStory)

                tvUserName.text = story.name
                tvStoryDate.text = dateFormatter(story.createdAt, TimeZone.getDefault().id)
                tvItemDescription.text = story.description.truncate(50)

                root.setOnClickListener {
                    navigateToDetail(story)
                }
            }
        }

        private fun navigateToDetail(story: ListStoryItem) {
            val detailIntent =
                Intent(binding.root.context, DetailStoryActivity::class.java).apply {
                    putExtra(DetailStoryActivity.EXTRA_TITLE, story.name)
                    putExtra(DetailStoryActivity.EXTRA_DESC, story.description)
                    putExtra(DetailStoryActivity.EXTRA_DATE, story.createdAt)
                    putExtra(DetailStoryActivity.EXTRA_THUMBNAIL, story.photoUrl)
                }
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    binding.root.context as Activity,
                    Pair(binding.ivStory, "image"),
                    Pair(binding.tvUserName, "username"),
                    Pair(binding.tvStoryDate, "date"),
                    Pair(binding.tvItemDescription, "description")
                )
            binding.root.context.startActivity(detailIntent, optionsCompat.toBundle())
        }
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val storyItem = getItem(position)
        storyItem?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ListStoryItem, newItem: ListStoryItem
            ): Boolean = oldItem == newItem
        }

        private fun String.truncate(maxLength: Int): String =
            if (this.length > maxLength) "${this.take(maxLength)}..." else this
    }
}
