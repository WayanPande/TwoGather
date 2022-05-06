package com.example.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.remote.Result
import com.example.storyapp.data.repository.AuthenticationRepositoryImpl
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.util.LoadingDialog

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loadingDialog = LoadingDialog(this)

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val pref = LoginPreferencesImpl.getInstance(dataStore)
        val authenticationRepository = AuthenticationRepositoryImpl()
        val loginViewModel =
            ViewModelProvider(
                this,
                LoginViewModelFactory(pref, authenticationRepository)
            )[LoginViewModel::class.java]

        binding.loginBtn.setOnClickListener {

            this.currentFocus?.let { view ->
                val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            loginViewModel.saveUserLoginData(email, password)
        }

        binding.signupBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        loginViewModel.response.observe(this) { response ->

            when (response) {
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT)
                        .show()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Login gagal!", Toast.LENGTH_SHORT)
                        .show()
                }
                is Result.Loading -> {
                    showLoading(true)
                }
            }
        }


        loginViewModel.getUserLoginData().observe(this) { token: String ->
            if (token != "") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        playAnimation()

    }

    private fun playAnimation() {

        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginBtn, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signupLayout, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(title, email, password, login, signup)
            start()
        }
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.startLoadingDialog()
        } else {
            loadingDialog.dismissDialog()
        }
    }
}