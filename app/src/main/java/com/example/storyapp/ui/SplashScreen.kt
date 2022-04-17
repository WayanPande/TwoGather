package com.example.storyapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding


    private val DELAY = 3000

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "name")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        val pref = LoginPreferences.getInstance(dataStore)
        val loginViewModel =
            ViewModelProvider(this, LoginViewModelFactory(pref))[LoginViewModel::class.java]

        loginViewModel.getUserLoginData().observe(this) { token: String ->
            if (token != "") {
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, DELAY.toLong())
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, DELAY.toLong())
            }
        }
    }
}