package com.example.storyapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.storyapp.MainCoroutineRule
import com.example.storyapp.data.remote.Result
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.LoginResult
import com.example.storyapp.fake.FakeAuthenticationRepository
import com.example.storyapp.fake.FakeLoginPreference
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
class LoginViewModelTest {


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(FakeLoginPreference(), FakeAuthenticationRepository())
    }

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Test
    fun `user login, result success`() {

        val susccessLoginResult = LoginResult("test", "user", "token")

        val successResponesLogin = LoginResponse(susccessLoginResult, false, "success")

        loginViewModel.saveUserLoginData("test@gmail.com", "123456")

        val expectedValue = Result.Success(successResponesLogin)

        val value = loginViewModel.response.getOrAwaitValue()

        assertThat(value).isEqualTo(expectedValue)

    }

    @Test
    fun `user login with no email, result error`() {

        val successResponesLogin =
            LoginResponse(null, false, "\"email\" is not allowed to be empty")

        loginViewModel.saveUserLoginData("", "123456")

        val expectedValue = Result.Success(successResponesLogin)

        val value = loginViewModel.response.getOrAwaitValue()

        assertThat(value).isEqualTo(expectedValue)

    }

    @Test
    fun `user login with no password, result error`() {

        val successResponesLogin =
            LoginResponse(null, false, "\"password\" is not allowed to be empty")

        loginViewModel.saveUserLoginData("test@gmail.com", "")

        val expectedValue = Result.Success(successResponesLogin)

        val value = loginViewModel.response.getOrAwaitValue()

        assertThat(value).isEqualTo(expectedValue)

    }

    @Test
    fun `user login with wrong email, result error`() {

        val successResponesLogin =
            LoginResponse(null, true, "User not found")

        loginViewModel.saveUserLoginData("testing@gmail.com", "123456")

        val expectedValue = Result.Success(successResponesLogin)

        val value = loginViewModel.response.getOrAwaitValue()

        assertThat(value).isEqualTo(expectedValue)

    }

    @Test
    fun `user login with wrong password, result error`() {

        val successResponesLogin =
            LoginResponse(null, true, "User not found")

        loginViewModel.saveUserLoginData("test@gmail.com", "12345678")

        val expectedValue = Result.Success(successResponesLogin)

        val value = loginViewModel.response.getOrAwaitValue()

        assertThat(value).isEqualTo(expectedValue)

    }

}