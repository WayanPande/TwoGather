package com.example.storyapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.storyapp.MainCoroutineRule
import com.example.storyapp.data.remote.Result
import com.example.storyapp.data.remote.response.StoryUploadResponse
import com.example.storyapp.fake.FakeUploadStoryRepository
import com.example.storyapp.getOrAwaitValue
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UploadStoryViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var uploadStoryViewModel: UploadStoryViewModel

    @Mock
    private lateinit var file: File

    private lateinit var imageMultipart: MultipartBody.Part

    @Before
    fun setUp() {
        uploadStoryViewModel = UploadStoryViewModel(FakeUploadStoryRepository())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        imageMultipart = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
    }

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `upload story without lon and lang success, return success`() {

        val response = StoryUploadResponse(false, "Story created successfully")
        val expectedValue = Result.Success(response)

        uploadStoryViewModel.uploadStory("test", imageMultipart, "test".toRequestBody())

        val value = uploadStoryViewModel.response.getOrAwaitValue()

        Truth.assertThat(value).isEqualTo(expectedValue)
    }

    @Test
    fun `upload story without token, return fail`() {

        val response = StoryUploadResponse(true, "Missing authentication")
        val expectedValue = Result.Success(response)

        uploadStoryViewModel.uploadStory("", imageMultipart, "test".toRequestBody())

        val value = uploadStoryViewModel.response.getOrAwaitValue()

        Truth.assertThat(value).isEqualTo(expectedValue)
    }

    @Test
    fun `upload story without correct image, return fail`() {

        val response = StoryUploadResponse(true, "photo should be Readable")
        val expectedValue = Result.Success(response)

        val requestImageFile = file.asRequestBody("image/png".toMediaTypeOrNull())
        imageMultipart = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        uploadStoryViewModel.uploadStory("token", imageMultipart, "test".toRequestBody())

        val value = uploadStoryViewModel.response.getOrAwaitValue()

        Truth.assertThat(value).isEqualTo(expectedValue)
    }


}