package com.example.storyapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.storyapp.MainCoroutineRule
import com.example.storyapp.data.remote.Result
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.StoryListResponse
import com.example.storyapp.fake.FakeStoryRepository
import com.example.storyapp.getOrAwaitValue
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoriesViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var storiesViewModel: StoriesViewModel

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        storiesViewModel = StoriesViewModel(FakeStoryRepository())
    }


    @Test
    fun `should return storyList with coordinate`() {

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

        val expextedResponse = Result.Success(storyResponse)

        storiesViewModel.getStoriesListWithCoordinate("token")

        val value = storiesViewModel.response.getOrAwaitValue()

        Truth.assertThat(value).isEqualTo(expextedResponse)

    }

    @Test
    fun `should return paging data`() = runTest {

        val value = storiesViewModel.getStoriesList("token")

        Truth.assertThat(value).isEqualTo("test")
    }
}