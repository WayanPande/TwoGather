package com.example.storyapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.Result
import com.example.storyapp.data.remote.response.StoryUploadResponse
import com.example.storyapp.data.repository.UploadStoryRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class UploadStoryViewModel(private val repository: UploadStoryRepository) : ViewModel() {

    private val _response = MutableLiveData<Result<StoryUploadResponse>>()
    val response: LiveData<Result<StoryUploadResponse>> = _response

    fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        description: RequestBody,
        lat: Float? = null,
        lon: Float? = null
    ) = viewModelScope.launch {
        _response.postValue(Result.Loading)
        val response = repository.uploadStory(token, image, description, lat, lon)
        _response.postValue(handleUploadResponse(response))
    }

    private fun handleUploadResponse(response: Response<StoryUploadResponse>): Result<StoryUploadResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Result.Success(resultResponse)
            }
        }
        return Result.Error(response.message())
    }

}