package com.ns.shortsnews.ui.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ns.shortsnews.domain.exception.ApiError
import com.ns.shortsnews.domain.models.LanguageData
import com.ns.shortsnews.domain.models.LanguagesResult
import com.ns.shortsnews.domain.models.UserSelectionResult
import com.ns.shortsnews.domain.models.UserSelectionsData
import com.ns.shortsnews.domain.usecase.base.UseCaseResponse
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserSelectionsDataUseCase
import com.ns.shortsnews.utils.AppPreference
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel constructor(
    private val languageDataUseCase: LanguageDataUseCase,
) : ViewModel() {

    companion object {
        val LOGIN = "login"
        val OTP = "otp"
        val PROFILE = "profile"
        val OTP_POP = "otp_pop"
        val MAIN_ACTIVITY = "main_activity"
        val LANGUAGES = "languages"
    }



    private val _fragmentStateFlow = MutableSharedFlow<Bundle?>()
    val fragmentStateFlow: SharedFlow<Bundle?> get() = _fragmentStateFlow

    //Language
    private val _languagesSuccessState = MutableStateFlow<List<LanguageData>>(emptyList())
    val LanguagesSuccessState: StateFlow<List<LanguageData>> get() = _languagesSuccessState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState




    fun updateFragment(fragmentType: String, bundle: Bundle) {
        viewModelScope.launch {
            bundle.putString("fragmentType", fragmentType)
            _fragmentStateFlow.emit(bundle)
        }
    }


    fun requestLanguagesApi() {
        languageDataUseCase.invoke(viewModelScope, null,
            object : UseCaseResponse<LanguagesResult> {
                override fun onSuccess(type: LanguagesResult) {
                    if (type.status) {
                        val languageStr = mutableListOf<String>()
                        for (item in type.data) {
                            if (item.default_select) {
                                languageStr.add(item.id)
                            }
                        }
                        AppPreference.saveSelectedLanguagesToPreference(languageStr)
                        _languagesSuccessState.value = type.data
                    }
                    else _errorState.value = "Empty from api server"
                }

                override fun onError(apiError: ApiError) {
                    var errorMsg = _errorState.value
                    if(errorMsg == null || errorMsg == apiError.getErrorMessage()) {
                        errorMsg = "${apiError.getErrorMessage()} "
                    } else {
                        errorMsg = "${apiError.getErrorMessage()}"
                    }
                    _errorState.value = errorMsg
                }

                override fun onLoading(isLoading: Boolean) {
                    _loadingState.value = isLoading
                }
            }
        )
    }


}