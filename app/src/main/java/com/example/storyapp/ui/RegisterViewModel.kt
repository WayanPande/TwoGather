package com.example.storyapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.Result
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.data.repository.AuthenticationRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterViewModel(private val authenticationRepository: AuthenticationRepository) :
    ViewModel() {

    private val _response = MutableLiveData<Result<RegisterResponse>>()
    val response: LiveData<Result<RegisterResponse>> = _response


    fun registerUser(name: String, email: String, password: String) = viewModelScope.launch {
        _response.postValue(Result.Loading)
        val response = authenticationRepository.registerUser(name, email, password)
        _response.postValue(handleRegisterResponse(response))
    }

    private fun handleRegisterResponse(response: Response<RegisterResponse>): Result<RegisterResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Result.Success(resultResponse)
            }
        }
        return Result.Error(response.message())
    }

}