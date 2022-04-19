package com.example.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ItemRowStoryBinding
import com.example.storyapp.ui.MainActivity
import com.example.storyapp.ui.StoriesDetailActivity

class StoriesListAdapter :
    PagingDataAdapter<ListStoryItem, StoriesListAdapter.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {

        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    class ListViewHolder(binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {

        private val ivStory: ImageView = binding.ivStory
        private val ivProfile: ImageView = binding.ivProfile
        private val tvName: TextView = binding.tvName
        private val tvFavorite: TextView = binding.tvFavorite
        private val tvComment: TextView = binding.tvComment

        fun bind(story: ListStoryItem) {
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_image_loading).error(R.drawable.ic_error))
                .into(ivStory)

            Glide.with(itemView.context)
                .load("https://avatars.dicebear.com/api/big-smile/${story.name}.png")
                .into(ivProfile)

            tvName.text = story.name
            tvComment.text = (10..2000).random().toString()
            tvFavorite.text = (10..2000).random().toString()

            itemView.setOnClickListener {

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(ivStory, "picture"),
                        Pair(ivProfile, "profilePict"),
                        Pair(tvName, "name")
                    )
                val intent = Intent(itemView.context, StoriesDetailActivity::class.java)
                intent.putExtra(MainActivity.USERNAME, story.name)
                intent.putExtra(MainActivity.PHOTOURL, story.photoUrl)
                intent.putExtra(MainActivity.DESCRIPTION, story.description)
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }

    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<ListStoryItem>() {
                override fun areItemsTheSame(
                    oldUser: ListStoryItem,
                    newUser: ListStoryItem
                ): Boolean {
                    return oldUser == newUser
                }

                override fun areContentsTheSame(
                    oldUser: ListStoryItem,
                    newUser: ListStoryItem
                ): Boolean {
                    return oldUser.id == newUser.id
                }
            }
    }
}