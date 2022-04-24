package com.example.storyapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.paging.StoryPagingSource
import kotlinx.coroutines.flow.Flow

class StoryRepository(private val apiService: ApiService) {

    fun getQuote(token: String): Flow<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).flow
    }

}