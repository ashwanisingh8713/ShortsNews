package com.io.domain.exception

/**
 * Created by Ashwani Kumar Singh on 06,February,2023.
 */

import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.exception.UNKNOWN_ERROR_MESSAGE
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Trace exceptions(api call or parse data or connection errors) &
 * depending on what exception returns [ApiErrorf]
 *
 * */
fun traceErrorException(throwable: Throwable?): ApiError {

    return when (throwable) {

        is HttpException -> {
            when (throwable.code()) {
                400 -> ApiError(
                    throwable.message(),
                    throwable.code(),
                    ApiError.ErrorStatus.BAD_REQUEST
                )
                401 -> ApiError(
                    throwable.message(),
                    throwable.code(),
                    ApiError.ErrorStatus.UNAUTHORIZED
                )
                403 -> ApiError(
                    throwable.message(),
                    throwable.code(),
                    ApiError.ErrorStatus.FORBIDDEN
                )
                404 -> ApiError(
                    throwable.message(),
                    throwable.code(),
                    ApiError.ErrorStatus.NOT_FOUND
                )
                405 -> ApiError(
                    throwable.message(),
                    throwable.code(),
                    ApiError.ErrorStatus.METHOD_NOT_ALLOWED
                )
                409 -> ApiError(
                    throwable.message(),
                    throwable.code(),
                    ApiError.ErrorStatus.CONFLICT
                )
                500 -> ApiError(
                    throwable.message(),
                    throwable.code(),
                    ApiError.ErrorStatus.INTERNAL_SERVER_ERROR
                )
                else -> ApiError(
                    UNKNOWN_ERROR_MESSAGE,
                    0,
                    ApiError.ErrorStatus.UNKNOWN_ERROR
                )
            }
        }

        is SocketTimeoutException -> {
            ApiError(throwable.message, ApiError.ErrorStatus.TIMEOUT)
        }

        is IOException -> {
            ApiError(throwable.message, ApiError.ErrorStatus.NO_CONNECTION)
        }

        else -> ApiError(UNKNOWN_ERROR_MESSAGE, 0, ApiError.ErrorStatus.UNKNOWN_ERROR)
    }
}