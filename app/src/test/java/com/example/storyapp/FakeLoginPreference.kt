package com.example.storyapp

import com.example.storyapp.ui.LoginPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLoginPreference: LoginPreferences {

    private val token = "token"

    override suspend fun saveUserLoginData(data: String) {

    }

    override fun getUserLoginData(): Flow<String> {
        return flow { emit(token) }
    }


}