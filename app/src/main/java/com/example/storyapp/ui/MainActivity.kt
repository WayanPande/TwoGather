package com.example.storyapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.R
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.adapter.StoriesListAdapter
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.util.LoadingDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val loadingDialog = LoadingDialog(this)
    private val storiesViewModel: StoriesViewModel by viewModels {
        ViewModelFactory(this)
    }
    private var adapter: StoriesListAdapter? = null
    private var token: String = ""

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val pref = LoginPreferences.getInstance(dataStore)
        val loginViewModel =
            ViewModelProvider(this, LoginViewModelFactory(pref))[LoginViewModel::class.java]


        loginViewModel.getUserLoginData().observe(this) { token: String ->

            if (token == "") {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            this.token = token
            initView()
            showRecycleList()
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

        binding.refreshLayout.setOnRefreshListener {
            showRecycleList()
            adapter?.notifyDataSetChanged()
            binding.rvStory.scrollToPosition(0)
            binding.refreshLayout.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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

                val mapsBtn = dialog.findViewById<TextView>(R.id.tv_maps)

                mapsBtn?.setOnClickListener {
                    val intent = Intent(this, MapsActivity::class.java)
                    intent.putExtra("TOKEN", token)
                    startActivity(intent)
                }

            }
        }
        return true
    }

    private fun initView() {
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        adapter = StoriesListAdapter()
        binding.rvStory.adapter = adapter!!.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter!!.retry()
            }
        )
    }


    private fun showRecycleList() {
        lifecycleScope.launch {
            storiesViewModel.getStoriesList(token).collectLatest { item ->
                adapter?.submitData(item)
            }
        }
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