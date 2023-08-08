package ru.netology.nework.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.AuthState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
@Inject constructor(private val appAuth: AppAuth) : ViewModel() {
    val authState: LiveData<AuthState> = appAuth
        .authStateFlow
        .asLiveData(Dispatchers.Default)

    val isAuthenticated: Boolean
        get() = appAuth.authStateFlow.value.id != 0L

    private var _checkIfAskedLogin = false
    val checkIfAskedToLogin: Boolean
        get() = _checkIfAskedLogin
    fun setCheckIfAskedLoginTrue() {
        _checkIfAskedLogin = true
    }
}