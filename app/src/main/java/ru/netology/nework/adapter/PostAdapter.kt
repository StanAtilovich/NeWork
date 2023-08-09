package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.databinding.PostListItemBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.util.AndroidUtils


interface OnPostButtonInteractionListener {
    fun onPostLike(post: Post)
    fun onPostRemove(post: Post)
    fun onPostEdit(post: Post)
}

class PostAdapter(private val interactionListener: OnPostButtonInteractionListener) :
    ListAdapter<Post, PostViewHolder>(PostDiffCallback) {

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

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

}


class PostViewHolder(
    private val postBinding: PostListItemBinding,
    private val interactionListener: OnPostButtonInteractionListener
) :
    RecyclerView.ViewHolder(postBinding.root) {
    fun bind(post: Post) {
        with(postBinding) {
            tVUserName.text = post.author
            tVPublished.text = AndroidUtils.formatMillisToDateString(post.published)
            tvContent.text = post.content
            btLike.isChecked = post.likedByMe
            btLike.text = post.likeCount.toString()

            btLike.setOnClickListener {
                interactionListener.onPostLike(post)
            }

            if (!post.ownedByMe) {
                btPostOptions.visibility = View.GONE
            } else {
                btPostOptions.visibility = View.VISIBLE
                btPostOptions.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.list_item_menu)
                        menu.setGroupVisible(R.id.list_item_modification, post.ownedByMe)
                        setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.action_delete -> {
                                    interactionListener.onPostRemove(post)
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
}
