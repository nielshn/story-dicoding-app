package com.dicoding.storydicodingapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storydicodingapp.R
import com.dicoding.storydicodingapp.adapter.LoadingStateAdapter
import com.dicoding.storydicodingapp.adapter.StoryAdapter
import com.dicoding.storydicodingapp.databinding.ActivityMainBinding
import com.dicoding.storydicodingapp.ui.login.LoginActivity
import com.dicoding.storydicodingapp.ui.maps.MapsActivity
import com.dicoding.storydicodingapp.ui.uploadStory.UploadStoryActivity
import com.dicoding.storydicodingapp.utils.applyWindowInsets

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val storyAdapter by lazy { StoryAdapter() }

    // ViewModel with factory
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        observeViewModel()
        setupRecyclerView()
        setupSwipeRefresh()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_maps -> openMaps()
            R.id.action_logout -> logout()
            R.id.action_setting -> openSettings()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }


    private fun setupUI() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        binding.root.applyWindowInsets()
        binding.fabAdd.setOnClickListener { openUploadStoryActivity() }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.apply {
            this.layoutManager = layoutManager
            addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
            adapter = storyAdapter.withLoadStateFooter(LoadingStateAdapter { storyAdapter.retry() })
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            storyAdapter.refresh()
            scrollToTop()
            showToast(getString(R.string.refresh))
        }
    }

    private fun observeViewModel() {
        mainViewModel.getSession().observe(this) { session ->
            if (!session.isLoggedIn) navigateToLogin()
        }

        mainViewModel.listStory.observe(this) { story ->
            storyAdapter.submitData(lifecycle, story)
            updateLoadingState(false)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun navigateToLogin() {
        Intent(this, LoginActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    private fun openMaps() {
        startActivity(Intent(this, MapsActivity::class.java))
    }
    private fun openSettings() {
        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }

    private fun logout() {
        mainViewModel.logout()
        finish()
    }

    private fun openUploadStoryActivity() {
        Intent(this, UploadStoryActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun scrollToTop() {
        (binding.rvStories.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(0, 0)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
