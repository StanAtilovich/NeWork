package ru.netology.nework.model

data class FeedStateModel(
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
)