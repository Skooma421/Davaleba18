package com.example.davaleba18.UI.register.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davaleba18.network.models.AuthRequest
import com.example.davaleba18.network.models.RegisterResponse
import com.example.davaleba18.network.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class RegisterViewModel: ViewModel() {
    val registerResponse = MutableLiveData<RegisterResponse>()
    val errorMessage = MutableLiveData<String>()

    fun register(email: String, password: String ){
        viewModelScope.launch {
            try {
                val response = RetrofitClient.authApiService.register(AuthRequest(email,password))
                if (email == "eve.holt@reqres.in"){
                    registerResponse.value = response
                }else {
                    errorMessage.value = "Invalid email"
                }
            }catch (e: Exception){
                errorMessage.value = e.message ?: "Unknown error"
            }
        }
    }
}