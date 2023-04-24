package com.ns.shortsnews.user.domain.usecase.base

import com.io.domain.exception.traceErrorException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Ashwani Kumar Singh on 07,February,2023.
 */
abstract class UseCase<Type, in Params>() where Type: Any {

    abstract suspend fun run(params: Params? = null):Type

    fun invoke(scope: CoroutineScope, params: Params?, onResult: UseCaseResponse<Type>?) {
        scope.launch {
            onResult?.onLoading(true)
            try {
                val result = run(params)
                onResult?.onLoading(false)
                onResult?.onSuccess(result)
            }
            catch (e: CancellationException) {
                e.printStackTrace()
                onResult?.onLoading(false)
                onResult?.onError(traceErrorException(e))
            }
            catch (e: Exception) {
                e.printStackTrace()
                onResult?.onLoading(false)
                onResult?.onError(traceErrorException(e))
            }
        }

    }

}