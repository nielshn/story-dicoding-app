package com.dicoding.storydicodingapp.di

import android.content.Context
import com.dicoding.storydicodingapp.data.StoryRepository
import com.dicoding.storydicodingapp.data.api.retrofit.ApiConfig
import com.dicoding.storydicodingapp.data.local.database.StoryDatabase
import com.dicoding.storydicodingapp.data.local.pref.LoginPreferences
import com.dicoding.storydicodingapp.data.local.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = LoginPreferences.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val storyDatabase = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(user.token)

        return StoryRepository.getInstance(apiService, pref, storyDatabase)
    }
}