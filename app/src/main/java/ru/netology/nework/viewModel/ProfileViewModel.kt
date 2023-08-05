package ru.netology.nework.viewModel

import androidx.lifecycle.ViewModel
import ru.netology.nework.auth.AppAuth

class ProfileViewModel: ViewModel() {

    fun onSignOut() {
        AppAuth.getInstance().removeAuth()
    }
}