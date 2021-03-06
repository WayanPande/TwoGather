package com.example.storyapp.data.remote.retrofit

import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.data.remote.response.StoryListResponse
import com.example.storyapp.data.remote.response.StoryUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("login")
    @FormUrlEncoded
    suspend fun loginRequest(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @POST("register")
    @FormUrlEncoded
    suspend fun registerRequest(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<StoryListResponse>

    @GET("stories")
    suspend fun getStoriesWithCoordinate(
        @Header("Authorization") token: String,
        @Query("location") location: Int,
    ): Response<StoryListResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float? = null,
        @Part("lon") lon: Float? = null
    ): Response<StoryUploadResponse>
}