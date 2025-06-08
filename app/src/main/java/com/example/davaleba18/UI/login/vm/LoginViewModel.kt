package com.example.davaleba18.UI.login.vm

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davaleba18.network.models.AuthRequest
import com.example.davaleba18.network.models.LoginResponse
import com.example.davaleba18.network.retrofit.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableStateFlow<Result<String>>(Result.success(""))
    val loginResult: StateFlow<Result<String>> = _loginResult.asStateFlow()

    fun login(apiEmail: String, apiPassword: String, rememberMe: Boolean, userMail: String) {
        viewModelScope.launch {
            try {
                val response =
                    RetrofitClient.authApiService.login(AuthRequest(apiEmail, apiPassword))
                _loginResult.value = success(response.token)
            } catch (e: Exception) {
                _loginResult.value = failure(e)
            }
        }
    }
    fun clearLoginState() {
        _loginResult.value = Result.success("")
    }
}

