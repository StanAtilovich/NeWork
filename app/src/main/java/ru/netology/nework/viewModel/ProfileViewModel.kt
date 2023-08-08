package ru.netology.nework.viewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.nework.auth.AppAuth
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val appAuth: AppAuth
): ViewModel() {
    fun onSignOut() {
        appAuth.removeAuth()
    }
}