package com.example.storyapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.StoryListResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.paging.StoryPagingSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface StoryRepository {
    fun getQuote(token: String): Flow<PagingData<ListStoryItem>>
    suspend fun getStoriesListWithCoordinate(token: String): Response<StoryListResponse>
}

class StoryRepositoryImpl : StoryRepository {

    override fun getQuote(token: String): Flow<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(ApiConfig.getApiService(), token)
            }
        ).flow
    }

    override suspend fun getStoriesListWithCoordinate(token: String): Response<StoryListResponse> =
        ApiConfig.getApiService().getStoriesWithCoordinate("Bearer $token", 1)

}