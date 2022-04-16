package com.example.storyapp.ui

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.ListStoryItem

class StoriesListAdapter(private val storiesList: ArrayList<ListStoryItem>) :
    RecyclerView.Adapter<StoriesListAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_row_story, parent, false)
        return ListViewHolder(view)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivStory: ImageView = itemView.findViewById(R.id.iv_story)
        private val ivProfile: ImageView = itemView.findViewById(R.id.iv_profile)
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvFavorite: TextView = itemView.findViewById(R.id.tv_favorite)
        private val tvComment: TextView = itemView.findViewById(R.id.tv_comment)

        fun bind(story: ListStoryItem) {
            Glide.with(itemView.context)
                .load(story.photoUrl)
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

        holder.bind(storiesList[position])

    }

    override fun getItemCount(): Int = storiesList.size


}