package com.example.Habitarium.data.network

import com.example.Habitarium.data.model.AuthResponse
import com.example.Habitarium.data.model.LoginRequest
import com.example.Habitarium.data.model.MaterialItem
import com.example.Habitarium.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HabitariumApiService {

    @GET("materials")
    suspend fun getMaterials(): List<MaterialItem>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
}