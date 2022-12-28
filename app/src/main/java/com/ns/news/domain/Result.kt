@file:Suppress("FunctionName")

package com.news.domain

import com.news.domain.Result.Failure
import com.news.domain.Result.Success


sealed class Result<out T> {
  data class Success<T>(val value: T): Result<T>()
  data class Failure(val cause: Throwable): Result<Nothing>()
}

inline fun <T> Result(block: () -> T): Result<T> = try {
  Success(block())
} catch (t: Throwable) {
  Failure(t)
}

fun <T> Result<T>.requireValue(): T = when (this) {
  is Success -> value
  is Failure -> throw cause
}


