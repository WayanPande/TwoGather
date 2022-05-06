package com.example.storyapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.storyapp.data.remote.Result
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.StoryListResponse
import com.example.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.Response

class StoriesViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _storyList = MutableLiveData<ArrayList<ListStoryItem>>()
    val storyList: LiveData<ArrayList<ListStoryItem>> = _storyList

    private val _response = MutableLiveData<Result<StoryListResponse>>()
    val response: LiveData<Result<StoryListResponse>> = _response

    fun getStoriesListWithCoordinate(token: String) = viewModelScope.launch {
        _response.postValue(Result.Loading)
        val response = storyRepository.getStoriesListWithCoordinate(token)
        _response.postValue(handleStoriesWithCoordinateResponse(response))
    }

    private fun handleStoriesWithCoordinateResponse(response: Response<StoryListResponse>): Result<StoryListResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                setStoryList(resultResponse.listStory as List<ListStoryItem>)
                return Result.Success(resultResponse)
            }
        }
        return Result.Error(response.message())
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