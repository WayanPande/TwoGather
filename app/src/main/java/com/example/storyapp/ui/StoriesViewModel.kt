package com.example.storyapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.StoryListResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoriesViewModel : ViewModel() {


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _storyList = MutableLiveData<ArrayList<ListStoryItem>>()
    val storyList: LiveData<ArrayList<ListStoryItem>> = _storyList


    fun getStoriesList(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStories("Bearer $token")
        client.enqueue(object : Callback<StoryListResponse> {
            override fun onResponse(
                call: Call<StoryListResponse>,
                response: Response<StoryListResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setStoryList(responseBody.listStory as List<ListStoryItem>)
                    }
                } else {
                    _isError.value = true
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<StoryListResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("STORIES", "onFailure: ${t.message}")
            }
        })
    }

    private fun setStoryList(data: List<ListStoryItem>) {
        val storyList = ArrayList<ListStoryItem>()
        for (story in data) {
            storyList.add(story)
        }

        _storyList.value = storyList
    }

}