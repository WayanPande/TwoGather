package com.example.storyapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.storyapp.MainCoroutineRule
import com.example.storyapp.data.remote.Result
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.fake.FakeAuthenticationRepository
import com.example.storyapp.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var registerViewModel: RegisterViewModel

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(FakeAuthenticationRepository())
    }

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `register new user with not empty field, returns not error`()  {

            val expectedValue = Result.Success(RegisterResponse(false, "User Created"))

            registerViewModel.registerUser("saya", "sayabangetmantap@gmail.com", "123456")

            val value = registerViewModel.response.getOrAwaitValue()

            assertThat(value).isEqualTo(expectedValue)

        }


    @Test
    fun `register new user with empty name field, returns error`() {

            val expectedValue =
                Result.Success(RegisterResponse(true, "\"name\" is not allowed to be empty"))

            registerViewModel.registerUser("", "sayabangetmantap@gmail.com", "123456")

            val value = registerViewModel.response.getOrAwaitValue()

            assertThat(value).isEqualTo(expectedValue)
        }

    @Test
    fun `register new user with empty email field, returns error`() {

            val expectedValue =
                Result.Success(RegisterResponse(true, "\"email\" is not allowed to be empty"))

            registerViewModel.registerUser("test", "", "123456")

            val value = registerViewModel.response.getOrAwaitValue()

            assertThat(value).isEqualTo(expectedValue)
        }

    @Test
    fun `register new user with empty password field, returns error`() {

            val expectedValue =
                Result.Success(RegisterResponse(true, "\"password\" is not allowed to be empty"))

            registerViewModel.registerUser("test", "sayabangetmantap@gmail.com", "")

            val value = registerViewModel.response.getOrAwaitValue()

            assertThat(value).isEqualTo(expectedValue)
        }

    @Test
    fun `register new user with password less than 6, returns error`() {

            val expectedValue = Result.Success(
                RegisterResponse(
                    true,
                    "Password must be at least 6 characters long"
                )
            )

            registerViewModel.registerUser("test", "sayabangetmantap@gmail.com", "123")

            val value = registerViewModel.response.getOrAwaitValue()

            assertThat(value).isEqualTo(expectedValue)
        }


}