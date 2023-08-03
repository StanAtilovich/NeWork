package ru.netology.nework.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nework.data.PostRepository
import ru.netology.nework.db.AppDb

import ru.netology.nework.dto.Post

class PostViewModel(application: Application): AndroidViewModel(application) {
    private val repository = PostRepository(AppDb.getInstance(application).postDao())

    init {
        loadPostsFromWeb()
    }

    private fun loadPostsFromWeb() {
        viewModelScope.launch {
            repository.loadPostsFromWeb()
        }
    }

    val postList = repository.getAllPosts()

    fun savePost(post: Post) {
        viewModelScope.launch {
            repository.createPost(post)
        }
    }


    fun deletePost(postId: Long) {
        viewModelScope.launch {
            repository.deletePost(postId)
        }
    }

}