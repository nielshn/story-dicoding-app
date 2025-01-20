package com.dicoding.storydicodingapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storydicodingapp.data.api.response.AddStoryResponse
import com.dicoding.storydicodingapp.data.api.response.ErrorResponse
import com.dicoding.storydicodingapp.data.api.response.ListStoryItem
import com.dicoding.storydicodingapp.data.api.response.LoginResponse
import com.dicoding.storydicodingapp.data.api.response.RegisterResponse
import com.dicoding.storydicodingapp.data.api.response.StoryResponse
import com.dicoding.storydicodingapp.data.api.retrofit.ApiConfig
import com.dicoding.storydicodingapp.data.api.retrofit.ApiService
import com.dicoding.storydicodingapp.data.local.database.StoryDatabase
import com.dicoding.storydicodingapp.data.local.pref.AuthToken
import com.dicoding.storydicodingapp.data.local.pref.LoginPreferences
import com.dicoding.storydicodingapp.result.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val preferences: LoginPreferences,
    private val storyDatabase: StoryDatabase
) {
    fun getStoriesWithLocation(): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val getToken = preferences.getSession().first()
            val apiService = ApiConfig.getApiService(getToken.token)
            val locationStoryResponse = apiService.getStoriesLocation()
            emit(Result.Success(locationStoryResponse))
        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(error, ErrorResponse::class.java)
            val errorMessage = errorResponse?.message ?: "An error occurred"
            emit(Result.Error("Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

    fun uploadStory(imageFile: File, description: String, lat: Double?, lon: Double?) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo", imageFile.name, requestImageFile
        )

        try {
            val user = preferences.getSession().first()
            val apiService = ApiConfig.getApiService(user.token)
            val addStoryResponse = apiService.addStory(multipartBody, requestBody)
            emit(Result.Success(addStoryResponse))
        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.toString()
            val errorResponse = Gson().fromJson(error, AddStoryResponse::class.java)
            val errorMessage = errorResponse?.message ?: "An error occurred"
            emit(Result.Error("Failed: $errorMessage"))
        }
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class) return try {
            Pager(config = PagingConfig(
                pageSize = 5
            ),
                remoteMediator = StoryRemoteMediator(storyDatabase, preferences),
                pagingSourceFactory = {
                    storyDatabase.storyDao().getAllStory()
                }).liveData
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun saveSession(user: AuthToken) {
        return preferences.saveSession(user)
    }

    fun getSession(): Flow<AuthToken> {
        return preferences.getSession()
    }

    suspend fun logout() {
        preferences.logout()
    }

    //    fun register(
//        username: String, email: String, password: String
//    ): LiveData<Result<RegisterResponse>> = liveData {
//        emit(Result.Loading)
//        try {
//            val registerResponse = apiService.register(username, email, password)
//            if (!registerResponse.error) {
//                emit(Result.Success(registerResponse))
//            } else {
//                emit(Result.Error(registerResponse.message))
//            }
//        } catch (e: HttpException) {
//            val error = e.response()?.errorBody()?.toString()
//            val errorResponse = Gson().fromJson(error, ErrorResponse::class.java)
//            val errorMessage = errorResponse?.message ?: "An error occurred"
//            emit(Result.Error("Registration Failed: $errorMessage"))
//        } catch (e: Exception) {
//            emit(Result.Error(e.toString()))
//        }
//    }
    fun register(
        username: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val registerResponse = apiService.register(username, email, password)
            if (!registerResponse.error) {
                emit(Result.Success(registerResponse))
            } else {
                emit(Result.Error(registerResponse.message))
            }
        } catch (e: HttpException) {
            val errorJson = e.response()?.errorBody()?.string()
            val errorResponse = errorJson?.let { Gson().fromJson(it, RegisterResponse::class.java) }
            val errorMessage = errorResponse?.message ?: "Unknown error occurred"
            emit(Result.Error("Registration Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("An unexpected error occurred: ${e.message}"))
        }
    }


    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            if (!response.error) {
                val token = AuthToken(
                    token = response.loginResult.token,
                    userId = response.loginResult.userId,
                    name = response.loginResult.name,
                    isLoggedIn = true
                )
                ApiConfig.token = response.loginResult.token
                preferences.saveSession(token)
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java).message
                    ?: "Unknown server error"
            } catch (parseError: Exception) {
                errorBody ?: "Unknown server error"
            }
            Log.e("StoryRepository", "API Error: $errorMessage")
            emit(Result.Error("Login Failed: $errorMessage"))
        } catch (e: Exception) {
            Log.e("StoryRepository", "General Error: ${e.message}")
            emit(Result.Error(e.toString()))
        }
    }


    companion object {
        private var INSTANCE: StoryRepository? = null
        fun getInstance(
            apiService: ApiService, preference: LoginPreferences, storyDatabase: StoryDatabase
        ): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryRepository(apiService, preference, storyDatabase)
            }
        }
    }
}