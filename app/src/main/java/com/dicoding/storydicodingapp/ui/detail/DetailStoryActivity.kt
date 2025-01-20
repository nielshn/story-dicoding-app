package com.dicoding.storydicodingapp.ui.detail


import android.icu.util.TimeZone
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.storydicodingapp.R
import com.dicoding.storydicodingapp.databinding.ActivityDetailStoryBinding
import com.dicoding.storydicodingapp.utils.applyWindowInsets
import com.dicoding.storydicodingapp.utils.dateFormatter

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
       binding.root.applyWindowInsets()
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            title = "Detail Story"
        }

        // Terapkan animasi transisi saat aktivitas dimulai
        window.sharedElementEnterTransition = TransitionInflater.from(this).inflateTransition(android.R.transition.move)

        val name = intent.getStringExtra(EXTRA_TITLE)
        val thumbnail = intent.getStringExtra(EXTRA_THUMBNAIL)
        val desc = intent.getStringExtra(EXTRA_DESC)
        val date = intent.getStringExtra(EXTRA_DATE)

        binding.tvItemTitle.text = name
        Glide.with(this)
            .load(thumbnail).skipMemoryCache(true).apply(
                RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error)
            ).into(binding.storyImageView)

        binding.tvItemDescription.text = desc
        binding.tvItemDate.text = dateFormatter(date.toString(), TimeZone.getDefault().id)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
                true
            }
            else -> false
        }
    }

    companion object {
        const val EXTRA_THUMBNAIL = "extra_thumbnail"
        const val EXTRA_DATE = "extra_date"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_DESC = "extra_desc"

    }
}