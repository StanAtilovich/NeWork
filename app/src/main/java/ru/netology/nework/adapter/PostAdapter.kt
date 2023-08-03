package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup

import android.widget.ListAdapter
import android.widget.PopupMenu

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.databinding.PostListItemBinding

import ru.netology.nework.dto.Post


interface OnButtonInteractionListener {
    fun onLike(post: Post)
    fun onRemove(post: Post)
}

class PostAdapter(private val interactionListener: OnButtonInteractionListener) : ListAdapter <Post, PostViewHolder> (PostDiffCallback) {

    companion object PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem == newItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val postBinding =
            PostListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return PostViewHolder(postBinding, interactionListener)
    }



}


class PostViewHolder(
    private val postBinding: PostListItemBinding,
    private val interactionListener: OnButtonInteractionListener
) :
    RecyclerView.ViewHolder(postBinding.root) {
    fun bind(post: Post) {
        with(postBinding) {
            tVUserName.text = post.author
            tVPublished.text = post.published.toString()
            tvContent.text = post.content
            btLike.isChecked = post.isLiked
            btLike.text = post.likeCount.toString()

            btLike.setOnClickListener {
                interactionListener.onLike(post)
            }

            btPostOptions.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_list_item_menu)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.action_delete -> {
                                interactionListener.onRemove(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}