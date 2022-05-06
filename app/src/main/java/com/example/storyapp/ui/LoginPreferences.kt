package com.example.storyapp.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LoginPreferences {
    suspend fun saveUserLoginData(data: String)
    fun getUserLoginData(): Flow<String>
}

class LoginPreferencesImpl private constructor(private val dataStore: DataStore<Preferences>) :
    LoginPreferences {

    private val TOKEN_KEY = stringPreferencesKey("token")

    override suspend fun saveUserLoginData(data: String) {
        dataStore.edit { preference ->
            preference[TOKEN_KEY] = data
        }
    }

    override fun getUserLoginData(): Flow<String> {
        return dataStore.data.map { preference ->
            preference[TOKEN_KEY] ?: ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LoginPreferencesImpl? = null

        fun getInstance(dataStore: DataStore<Preferences>): LoginPreferencesImpl {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginPreferencesImpl(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}