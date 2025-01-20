package com.dicoding.storydicodingapp.ui.signup

import androidx.lifecycle.ViewModel
import com.dicoding.storydicodingapp.data.StoryRepository

class SignupViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun register(username: String, email: String, password: String) =
        storyRepository.register(username, email, password)
}