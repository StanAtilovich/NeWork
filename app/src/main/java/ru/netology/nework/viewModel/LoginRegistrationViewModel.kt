package ru.netology.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.data.SignInUpRepository
import ru.netology.nework.error.AppError
import ru.netology.nework.model.FeedStateModel
import javax.inject.Inject

@HiltViewModel
class LoginRegistrationViewModel @Inject constructor(
    private val repository: SignInUpRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    private val _isSignedIn = MutableLiveData(false)
    val isSignedIn: LiveData<Boolean>
        get() = _isSignedIn

    private val _dataState = MutableLiveData(FeedStateModel())
    val dataState: LiveData<FeedStateModel>
        get() = _dataState

    fun invalidateSignedInState() {
        _isSignedIn.value = false
    }

    fun invalidateDataState() {
        _dataState.value = FeedStateModel()
    }

    fun onSignIn(login: String, password: String) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedStateModel(isLoading = true)

                val idAndToken = repository.onSignIn(login, password)
                val id = idAndToken.userId ?: 0L
                val token = idAndToken.token ?: "N/A"
                appAuth.setAuth(id = id, token = token)

                _dataState.value = FeedStateModel(isLoading = false)
                _isSignedIn.value = true
            } catch (e: Exception) {
                _dataState.value = (FeedStateModel(
                    hasError = true,
                    errorMessage = AppError.getMessage(e)
                ))
            }
        }
    }

    fun onSignUp(login: String, password: String, userName: String) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedStateModel(isLoading = true)
                repository.onSignUp(login, password, userName)
                _dataState.value = FeedStateModel(isLoading = false)
            } catch (e: Exception) {
                _dataState.value = (FeedStateModel(
                    hasError = true,
                    errorMessage = AppError.getMessage(e)
                ))
            }
        }
    }

}