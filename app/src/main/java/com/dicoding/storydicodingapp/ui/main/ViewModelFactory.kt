package com.dicoding.storydicodingapp.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storydicodingapp.data.StoryRepository
import com.dicoding.storydicodingapp.di.Injection
import com.dicoding.storydicodingapp.ui.login.LoginViewModel
import com.dicoding.storydicodingapp.ui.maps.MapsViewModel
import com.dicoding.storydicodingapp.ui.signup.SignupViewModel
import com.dicoding.storydicodingapp.ui.uploadStory.UploadStoryViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MainViewModel::class.java -> MainViewModel(repository) as T
            LoginViewModel::class.java -> LoginViewModel(repository) as T
            SignupViewModel::class.java -> SignupViewModel(repository) as T
            UploadStoryViewModel::class.java -> UploadStoryViewModel(repository) as T
            MapsViewModel::class.java -> MapsViewModel(repository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}