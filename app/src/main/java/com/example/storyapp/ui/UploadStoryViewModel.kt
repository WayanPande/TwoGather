package com.example.storyapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.remote.response.StoryUploadResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadStoryViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError


    fun uploadStory(token: String, image: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().uploadStory("Bearer $token", image, description)
        client.enqueue(object : Callback<StoryUploadResponse> {
            override fun onResponse(
                call: Call<StoryUploadResponse>,
                response: Response<StoryUploadResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _isError.value = false
                    }
                } else {
                    _isError.value = true
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<StoryUploadResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
                Log.e("UPLOAD", "onFailure: ${t.message}")
            }
        })
    }

}