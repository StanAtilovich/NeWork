package ru.netology.nework.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.data.PostRepository
import ru.netology.nework.db.AppDb
import ru.netology.nework.dto.Post
import ru.netology.nework.error.AppError
import ru.netology.nework.model.FeedStateModel
import javax.inject.Inject


@ExperimentalPagingApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth
) : ViewModel() {

    private val _dataState = MutableLiveData(FeedStateModel())
    val dataState: LiveData<FeedStateModel>
        get() = _dataState

    fun invalidateDataState() {
        _dataState.value = FeedStateModel()
    }

    private val _editedPost = MutableLiveData<Post?>(null)
    val editedPost: LiveData<Post?>
        get() = _editedPost


    private val cached = repository.getAllPosts().cachedIn(viewModelScope)

    @ExperimentalCoroutinesApi
    val postList: Flow<PagingData<Post>> = appAuth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { postList ->
                postList.map { it.copy(ownedByMe = it.authorId == myId) }
            }
        }


    fun editPost(editedPost: Post) {
        _editedPost.value = editedPost
    }

    fun invalidateEditPost() {
        _editedPost.value = null
    }


    fun savePost(post: Post) {
        viewModelScope.launch {
            try {
                _dataState.value = (FeedStateModel(isLoading = true))
                repository.createPost(post)
                _dataState.value = (FeedStateModel(isLoading = false))
            } catch (e: Exception) {
                _dataState.value = (FeedStateModel(
                    hasError = true,
                    errorMessage = AppError.getMessage(e)
                ))
            } finally {
                invalidateEditPost()
            }
        }
    }


    fun likePost(post: Post){
        viewModelScope.launch {
            try{
                _dataState.value = FeedStateModel()
                repository.likePost(post)
            } catch (e : Exception) {
                _dataState.value = (FeedStateModel(
                    hasError = true,
                    errorMessage = AppError.getMessage(e)
                ))
            }
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = (FeedStateModel(isLoading = true))
                repository.deletePost(postId)
                _dataState.value = (FeedStateModel(isLoading = false))
            } catch (e: Exception) {
                _dataState.value = (FeedStateModel(
                    hasError = true,
                    errorMessage = AppError.getMessage(e)
                ))
            }
        }
    }

}