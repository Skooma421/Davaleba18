package com.example.davaleba18.network.api

import com.example.davaleba18.network.models.AuthRequest
import com.example.davaleba18.network.models.LoginResponse
import com.example.davaleba18.network.models.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("v3/542e71ad-2f52-4831-9509-ba629973ba6d")
    suspend fun login(@Body request: AuthRequest): LoginResponse

    @POST("v3/7d70e240-c481-450b-95d2-fdf5f813ed3c")
    suspend fun register(@Body request: AuthRequest): RegisterResponse
}