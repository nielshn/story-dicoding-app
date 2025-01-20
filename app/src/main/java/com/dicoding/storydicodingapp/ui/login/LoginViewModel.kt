package com.dicoding.storydicodingapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storydicodingapp.data.StoryRepository
import com.dicoding.storydicodingapp.data.local.pref.AuthToken
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun login(email: String, password: String) = storyRepository.login(email, password)
    fun saveUserSession(authToken: AuthToken) {
        viewModelScope.launch {
            storyRepository.saveSession(authToken)
        }
    }
}