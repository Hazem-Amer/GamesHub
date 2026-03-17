package com.example.gameshub.util

sealed class AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : AppResult<Nothing>()
}

