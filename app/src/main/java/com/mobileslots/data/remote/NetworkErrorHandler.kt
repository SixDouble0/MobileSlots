package com.mobileslots.data.remote

sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String, val code: Int? = null) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}

object NetworkErrorHandler {
    fun <T> handleException(exception: Exception): NetworkResult<T> {
        return when (exception) {
            is java.net.UnknownHostException -> {
                NetworkResult.Error("No internet connection")
            }
            is java.net.SocketTimeoutException -> {
                NetworkResult.Error("Connection timeout")
            }
            is retrofit2.HttpException -> {
                NetworkResult.Error("Server error: ${exception.code()}", exception.code())
            }
            else -> {
                NetworkResult.Error(exception.message ?: "Unknown error occurred")
            }
        }
    }

    suspend fun <T> safeApiCall(apiCall: suspend () -> retrofit2.Response<T>): NetworkResult<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error("Error: ${response.code()} ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }
}
