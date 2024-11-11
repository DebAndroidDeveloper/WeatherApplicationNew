package com.mycompany.weatherapplication.network

/**
 * Sealed class, enumerates all [Result] types that can be returned by the Repository Layer.
 */
sealed class Result<T>(val data: T? = null) {

    /**
     * Indicates success of the network request, property [Success.data] can carry response payload
     * if available for this request.
     */
    class Success<T>(data : T?) : Result<T>(data) {
        override fun toString(): String {
            return  "Success(data='$data')"
        }
    }

    class Error<T>(val errorMessage: String, val errorCode: Int = 0) : Result<T>() {
        override fun toString(): String {
            return "Error(errorMessage='$errorMessage', errorCode=$errorCode"
        }
    }

}