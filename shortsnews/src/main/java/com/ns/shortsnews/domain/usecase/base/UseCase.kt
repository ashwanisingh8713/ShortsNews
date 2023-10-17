package com.ns.shortsnews.domain.usecase.base

import android.util.Log
import com.io.domain.exception.traceErrorException
import com.ns.shortsnews.MainApplication
import com.ns.shortsnews.utils.IntentLaunch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * Created by Ashwani Kumar Singh on 07,February,2023.
 */
abstract class UseCase<Type, in Params> where Type: Any {

    abstract suspend fun run(params: Params? = null):Type

    fun invoke(scope: CoroutineScope, params: Params?, onResult: UseCaseResponse<Type>?) {
        scope.launch {
            onResult?.onLoading(true)
            try {
                val result = run(params)
                onResult?.onSuccess(result)
                onResult?.onLoading(false)
            }
            catch (e: CancellationException) {
                e.printStackTrace()
                onResult?.onError(traceErrorException(e))
                onResult?.onLoading(false)
            }
            catch (e: Exception) {
                if(e is HttpException) {
                    val httpCode = e.code()
                    if(httpCode == 401) {
                        Log.i("Logout", "401")
                        // Logout
                        IntentLaunch.logoutInfoDialog()
                    }
                }
                else {
                    onResult?.onError(traceErrorException(e))
                    onResult?.onLoading(false)
                }
            }
        }

    }

}