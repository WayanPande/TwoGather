package com.example.storyapp.fake

import androidx.paging.PagingData
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.StoryListResponse
import com.example.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FakeStoryRepository : StoryRepository {

    val listStoryItem = List(10) {
        ListStoryItem(
            "url $it",
            "create at $it",
            "name $it",
            "desc $it",
            it.toDouble(),
            "id $it",
            it.toDouble()
        )
    }

    val storyResponse = StoryListResponse(listStoryItem, false, "test")

    override fun getQuote(token: String): Flow<PagingData<ListStoryItem>> {
        return flow { emit(PagingData.from(listStoryItem)) }
    }

    override suspend fun getStoriesListWithCoordinate(token: String): Response<StoryListResponse> {
        return Response.success(storyResponse)
    }
}