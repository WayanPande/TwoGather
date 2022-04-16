package com.example.storyapp.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityStoriesDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class StoriesDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoriesDetailBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoriesDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.title = getString(R.string.stories_detail_activity_title)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val name = intent.getStringExtra(MainActivity.USERNAME)
        val photoUrl = intent.getStringExtra(MainActivity.PHOTOURL)
        val description = intent.getStringExtra(MainActivity.DESCRIPTION)

        binding.tvName.text = name
        binding.tvDescription.text = description

        postponeEnterTransition()
        Glide.with(binding.root)
            .load(photoUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

            })
            .into(binding.ivStory)

        Glide.with(binding.root)
            .load("https://avatars.dicebear.com/api/big-smile/${name}.png")
            .into(binding.ivProfile)
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
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

}