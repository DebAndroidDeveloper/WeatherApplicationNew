package com.mycompany.weatherapplication.network

import kotlinx.coroutines.yield
import retrofit2.Response

/**
 * Network executor class to fetch the response and set response to the Result sealed class
 */
object NetworkExecutor {
    val TAG = NetworkExecutor::class.java.canonicalName

    suspend fun<T> execute(block: suspend () -> Response<T>) : Result<T> {
        return execute(block,{it})
    }

    /**
     * Executes request block and returns the result after applying transformer function.
     *
     * This is a blocking call, should never be executed on main event thread.
     */
    suspend fun<T, R> execute(
        request: suspend () -> Response<T>,
        transformer: ((T) -> R?)
    ): Result<R> {
        return try {
            yield() // Check for canceled state before executing the request.
            val response = request.invoke()
            if(response.isSuccessful) {
                Result.Success(response.body()?.let(transformer::invoke))
            } else {
                Result.Error("${response.message()} with error code ${response.code()}")
            }
        } catch (exception: Exception) {
            Result.Error(exception.message ?: "")
        }

    }
}