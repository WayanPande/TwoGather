package com.example.storyapp.fake

import com.example.storyapp.data.remote.response.StoryUploadResponse
import com.example.storyapp.data.repository.UploadStoryRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class FakeUploadStoryRepository : UploadStoryRepository {


    private val successResponse = StoryUploadResponse(false, "Story created successfully")
    private val noTokenGivenResponse = StoryUploadResponse(true, "Missing authentication")
    private val imageFailGivenResponse = StoryUploadResponse(true, "photo should be Readable")


    override suspend fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        lon: Float?
    ): Response<StoryUploadResponse> {

        if (token.isEmpty() || token == "") return Response.success(noTokenGivenResponse)

        if (image.body.contentType() != "image/jpeg".toMediaTypeOrNull()) return Response.success(
            imageFailGivenResponse
        )

        return Response.success(successResponse)
    }
}