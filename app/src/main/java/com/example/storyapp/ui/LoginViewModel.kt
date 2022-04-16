package com.example.storyapp.ui


import android.util.Log
import androidx.lifecycle.*
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: LoginPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError


    fun getUserLoginData(): LiveData<String> {
        return pref.getUserLoginData().asLiveData()
    }

    fun logoutUser() {
        viewModelScope.launch {
            pref.saveUserLoginData("")
        }
    }


    fun saveUserLoginData(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().loginRequest(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {

                        when (responseBody.error) {
                            true -> {
                                Log.e("LOGINGAGAL", responseBody.toString())
                                _isError.value = true
                            }
                            false -> {
                                viewModelScope.launch {
                                    responseBody.loginResult?.token?.let { pref.saveUserLoginData(it) }
                                }
                                _isError.value = false
                            }
                            else -> {
                                _isError.value = true
                                Log.e("LOGINGAGAL", responseBody.toString())
                            }
                        }
                    }
                } else {
                    _isError.value = true
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
                Log.e("LOGIN", "onFailure: ${t.message}")
            }
        })
    }


}