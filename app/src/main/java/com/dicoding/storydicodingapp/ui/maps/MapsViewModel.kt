package com.dicoding.storydicodingapp.ui.maps

import androidx.lifecycle.ViewModel
import com.dicoding.storydicodingapp.data.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository): ViewModel (){
    fun getStoryLocation() = storyRepository.getStoriesWithLocation()
}