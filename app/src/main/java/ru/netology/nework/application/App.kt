package ru.netology.nework.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.netology.nework.auth.AppAuth

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
    }
}