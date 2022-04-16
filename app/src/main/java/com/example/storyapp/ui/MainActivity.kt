package com.example.storyapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.util.LoadingDialog
import com.google.android.material.bottomsheet.BottomSheetDialog


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val loadingDialog = LoadingDialog(this)

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val pref = LoginPreferences.getInstance(dataStore)
        val loginViewModel =
            ViewModelProvider(this, LoginViewModelFactory(pref))[LoginViewModel::class.java]
        val storiesViewModel = ViewModelProvider(this)[StoriesViewModel::class.java]

        loginViewModel.getUserLoginData().observe(this) { token: String ->

            if (token == "") {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            storiesViewModel.getStoriesList(token)

        }

        storiesViewModel.storyList.observe(this) {

            if (it.size > 1) {
                showRecycleList(it)
            } else {
                Toast.makeText(this, "No data available...", Toast.LENGTH_LONG).show()
            }

        }

        storiesViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        storiesViewModel.isError.observe(this) {
            if (it) {
                Toast.makeText(this, "Fail to load data!", Toast.LENGTH_LONG).show()
            }
        }

        binding.floatBtn.setOnClickListener {
            val intent = Intent(this, AddStoriesActivity::class.java)
            startActivity(intent)
        }

        binding.rvStory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 10 && binding.floatBtn.isShown) {
                    binding.floatBtn.hide()
                }

                if (dy < -10 && !binding.floatBtn.isShown) {
                    binding.floatBtn.show()
                }

                if (!recyclerView.canScrollVertically(-1)) {
                    binding.floatBtn.show()
                }
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_toggle -> {

                val pref = LoginPreferences.getInstance(dataStore)
                val loginViewModel =
                    ViewModelProvider(this, LoginViewModelFactory(pref))[LoginViewModel::class.java]

                val view: View = layoutInflater.inflate(R.layout.item_bottom_sheet, null)
                val dialog = BottomSheetDialog(this)
                dialog.setContentView(view)
                dialog.show()

                val logoutBtn = dialog.findViewById<TextView>(R.id.tv_logout)

                logoutBtn?.setOnClickListener {
                    loginViewModel.logoutUser()
                }

            }
        }
        return true
    }


    private fun showRecycleList(storiesList: ArrayList<ListStoryItem>) {
        binding.rvStory.setHasFixedSize(true)
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        val listUserAdapter = StoriesListAdapter(storiesList)
        binding.rvStory.adapter = listUserAdapter
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.startLoadingDialog()
        } else {
            loadingDialog.dismissDialog()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    companion object {
        const val USERNAME = "username"
        const val PHOTOURL = "photourl"
        const val DESCRIPTION = "description"
    }
}