package com.ns.shortsnews.user.domain.usecase.base

import com.ns.shortsnews.user.domain.exception.ApiError

interface UseCaseResponse<Type> {
    fun onSuccess(type: Type)
    fun onError(apiError: ApiError)
    fun onLoading(isLoading: Boolean)
}