package com.example.davaleba18.UI.login.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davaleba18.network.models.AuthRequest
import com.example.davaleba18.network.retrofit.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Thread.State
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
                val errorMessage = when {
                    e.message?.contains("HTTPS 400") == true -> "Invalid credentials"
                    e.message?.contains("network") == true -> "Network error"
                    else -> e.message ?: "Login failed"
                }
                _loginResult.value = Result.failure(Exception(errorMessage))
            }
        }
    }

    fun clearLoginState() {
        _loginResult.value = Result.success("")
    }
}

