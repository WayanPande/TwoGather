package com.example.storyapp.ui


import androidx.lifecycle.*
import com.example.storyapp.data.repository.AuthenticationRepository
import com.example.storyapp.data.remote.Result
import com.example.storyapp.data.remote.response.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(
    private val pref: LoginPreferences,
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val _response = MutableLiveData<Result<LoginResponse>>()
    val response: LiveData<Result<LoginResponse>> = _response


    fun getUserLoginData(): LiveData<String> {
        return pref.getUserLoginData().asLiveData()
    }

    fun logoutUser() {
        viewModelScope.launch {
            pref.saveUserLoginData("")
        }
    }


    fun saveUserLoginData(email: String, password: String) = viewModelScope.launch {
        _response.postValue(Result.Loading)

        val response = authenticationRepository.loginUser(email, password)
        _response.postValue(handleLoginResponse(response))
    }

    private fun handleLoginResponse(response: Response<LoginResponse>): Result<LoginResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                viewModelScope.launch {
                    resultResponse.loginResult?.token?.let { pref.saveUserLoginData(it) }
                }

                return Result.Success(resultResponse)
            }
        }
        return Result.Error(response.message())
    }


}