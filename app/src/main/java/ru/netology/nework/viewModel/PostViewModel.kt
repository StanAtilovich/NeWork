package ru.netology.nework.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
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

    @ExperimentalCoroutinesApi
    val postList: LiveData<List<Post>> = AppAuth.getInstance()
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.getAllPosts()
                .map { postList ->
                    postList.map {
                        it.copy(ownedByMe = myId == it.authorId)
                    }
                }
        }.asLiveData(Dispatchers.Default)

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