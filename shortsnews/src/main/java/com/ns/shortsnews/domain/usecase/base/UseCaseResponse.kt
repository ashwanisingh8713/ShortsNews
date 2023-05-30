package com.ns.shortsnews.domain.usecase.base

import com.ns.shortsnews.domain.exception.ApiError

interface UseCaseResponse<Type> {
    fun onSuccess(type: Type)
    fun onError(apiError: ApiError)
    fun onLoading(isLoading: Boolean)
}