package com.example.storyapp.fake

import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.LoginResult
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.data.repository.AuthenticationRepository
import retrofit2.Response

class FakeAuthenticationRepository : AuthenticationRepository {


    private val successResponse = RegisterResponse(false, "User Created")
    private val nameFailResponse = RegisterResponse(true, "\"name\" is not allowed to be empty")
    private val emailFailResponse = RegisterResponse(true, "\"email\" is not allowed to be empty")
    private val passwordFailResponse =
        RegisterResponse(true, "\"password\" is not allowed to be empty")
    private val passwordLessThanSixFailResponse =
        RegisterResponse(true, "Password must be at least 6 characters long")

    private val email = "test@gmail.com"
    private val password = "123456"

    private val successLoginResult = LoginResult("test", "user", "token")

    private val unsuccessfulLoginResponse = LoginResponse(null, true, "User not found")
    private val successResponesLogin = LoginResponse(successLoginResult, false, "success")
    private val emailFailResponseLogin = LoginResponse(null, false, "\"email\" is not allowed to be empty")
    private val passwordFailResponseLogin = LoginResponse(null, false, "\"password\" is not allowed to be empty")

    override suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): Response<RegisterResponse> {

        if (name.isEmpty() || name == "") return Response.success(nameFailResponse)

        if (email.isEmpty() || email == "") return Response.success(emailFailResponse)


        if (password.isEmpty() || password == "") return Response.success(passwordFailResponse)

        if (password.length < 6) return Response.success(passwordLessThanSixFailResponse)



        return Response.success(successResponse)
    }


    override suspend fun loginUser(email: String, password: String): Response<LoginResponse> {


        if (email.isEmpty() || email == "") return Response.success(emailFailResponseLogin)

        if (password.isEmpty() || password == "") return Response.success(passwordFailResponseLogin)

        if (email != this.email || password != this.password) return Response.success(unsuccessfulLoginResponse)

        return Response.success(successResponesLogin)

    }

}