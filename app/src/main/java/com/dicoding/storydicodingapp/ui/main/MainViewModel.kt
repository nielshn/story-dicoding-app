package com.dicoding.storydicodingapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storydicodingapp.data.StoryRepository
import com.dicoding.storydicodingapp.data.api.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val storyRepository: StoryRepository): ViewModel(){
    val listStory: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStories().cachedIn(viewModelScope)

    fun getSession() = storyRepository.getSession().asLiveData()

    fun logout() {
        viewModelScope.launch {
            storyRepository.logout()
        }
    }
}