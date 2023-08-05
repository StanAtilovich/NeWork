package ru.netology.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.AuthState

class AuthViewModel : ViewModel() {
    val authState: LiveData<AuthState> = AppAuth.getInstance()
        .authStateFlow
        .asLiveData(Dispatchers.Default)

    val isAuthenticated: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L
}