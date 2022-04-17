package com.example.storyapp.ui

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.storyapp.data.remote.StoryRepository
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.di.Injection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoriesViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

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