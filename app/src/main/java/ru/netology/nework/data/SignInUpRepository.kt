package ru.netology.nework.data

import ru.netology.nework.api.PostApi
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.NetworkError
import ru.netology.nework.error.UndefinedError
import ru.netology.nework.model.AuthJsonModel
import java.io.IOException


class SignInUpRepository {

    suspend fun onSignIn(login: String, password: String): AuthJsonModel {
        try {
            val response = PostApi.retrofitService.signIn(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code())
            }
            return response.body() ?: throw ApiError(response.code())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            print(e.message)
            throw UndefinedError
        }
    }

    suspend fun onSignUp(login: String, password: String, userName: String): AuthJsonModel {
        try {
            val response = PostApi.retrofitService.signUp(login, password, userName)
            if (!response.isSuccessful) {
                println("RESPONSE CODE IS ${response.code()}")
                println("RESPONSE MESSAGE : ${response.message()}")
                throw ApiError(response.code())
            }
            return response.body() ?: throw ApiError(response.code())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            print(e.message)
            throw UndefinedError
        }
    }
}