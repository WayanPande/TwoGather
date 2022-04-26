package com.example.storyapp.data.repository

import com.example.storyapp.data.remote.response.StoryUploadResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface UploadStoryRepository {
    suspend fun uploadStory(token: String, image: MultipartBody.Part, description: RequestBody, lat: Float? = null, lon: Float? = null): Response<StoryUploadResponse>
}

class UploadStoryRepositoryImpl: UploadStoryRepository {
    override suspend fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        lon: Float?
    ): Response<StoryUploadResponse> = ApiConfig.getApiService().uploadStory("Bearer $token", image, description, lat, lon)

}