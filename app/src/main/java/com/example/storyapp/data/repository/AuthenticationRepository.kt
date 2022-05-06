package com.example.storyapp.data.repository

import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import retrofit2.Response

interface AuthenticationRepository {
     suspend fun registerUser(name: String, email: String, password: String): Response<RegisterResponse>

     suspend fun loginUser(email: String, password: String): Response<LoginResponse>
}

 class AuthenticationRepositoryImpl: AuthenticationRepository {

    override suspend fun registerUser(name: String, email: String, password: String) = ApiConfig.getApiService().registerRequest(name, email, password)

    override suspend fun loginUser(email: String, password: String) = ApiConfig.getApiService().loginRequest(email, password)

}