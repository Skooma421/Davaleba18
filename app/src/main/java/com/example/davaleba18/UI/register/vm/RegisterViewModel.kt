package com.example.davaleba18.UI.register.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davaleba18.network.models.AuthRequest
import com.example.davaleba18.network.models.RegisterResponse
import com.example.davaleba18.network.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    val registerResponse = MutableLiveData<RegisterResponse>()
    val errorMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()

    fun register(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitClient.authApiService.register(AuthRequest(email, password))
                if (email == "eve.holt@reqres.in") {
                    registerResponse.value = response
                } else {
                    errorMessage.value = "Invalid email"
                }
            } catch (e: Exception) {
                errorMessage.value = when {
                    e.message?.contains("HTTP 400") == true -> "User already exists"
                    e.message?.contains("network") == true -> "Network error"
                    else -> e.message ?: "Registration failed"
                }
            } finally {
                isLoading.value = false
            }
        }
    }
}