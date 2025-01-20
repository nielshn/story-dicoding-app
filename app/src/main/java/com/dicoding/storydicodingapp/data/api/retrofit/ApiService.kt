package com.dicoding.storydicodingapp.data.api.retrofit

import com.dicoding.storydicodingapp.data.api.response.AddStoryResponse
import com.dicoding.storydicodingapp.data.api.response.LoginResponse
import com.dicoding.storydicodingapp.data.api.response.RegisterResponse
import com.dicoding.storydicodingapp.data.api.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): AddStoryResponse

    @GET("stories")
    suspend fun getStoriesLocation(
        @Header("location") location: Int = 1,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 100
    ): StoryResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryResponse
}