package com.example.storyapp.data.remote

import com.example.storyapp.data.remote.retrofit.ApiConfig

class AuthenticationRepository {

    suspend fun registerUser(name: String, email: String, password: String) = ApiConfig.getApiService().registerRequest(name, email, password)

}