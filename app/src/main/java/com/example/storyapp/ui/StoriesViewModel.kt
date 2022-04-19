package com.example.storyapp.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.storyapp.data.remote.StoryRepository
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.StoryListResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.di.Injection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoriesViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _storyList = MutableLiveData<ArrayList<ListStoryItem>>()
    val storyList: LiveData<ArrayList<ListStoryItem>> = _storyList


    fun getStoriesListWithCoordinate(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStoriesWithCoordinate("Bearer $token", 1)
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

    fun getStoriesList(token: String): Flow<PagingData<ListStoryItem>> {
        return storyRepository.getQuote(token)
            .map { pagingData ->
                pagingData.map {
                    ListStoryItem(
                        it.photoUrl,
                        it.createdAt,
                        it.name,
                        it.description,
                        it.lon,
                        it.id,
                        it.lat
                    )
                }
            }
            .cachedIn(viewModelScope)
    }
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoriesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoriesViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}