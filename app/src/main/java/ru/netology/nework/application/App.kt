package ru.netology.nework.application

import android.app.Application
import ru.netology.nework.auth.AppAuth

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        AppAuth.initializeAppAuth(this)
    }
}