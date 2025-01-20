package com.dicoding.storydicodingapp.ui.uploadStory

import androidx.lifecycle.ViewModel
import com.dicoding.storydicodingapp.data.StoryRepository
import java.io.File

class UploadStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun uploadStory(description: String, imageFile: File, lat: Double?, lon: Double?) =
        storyRepository.uploadStory(imageFile, description, lat, lon)
}