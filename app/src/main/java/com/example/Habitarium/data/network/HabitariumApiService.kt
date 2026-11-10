package com.example.Habitarium.data.network

import com.example.Habitarium.data.model.MaterialItem
import retrofit2.http.GET

interface HabitariumApiService {

    @GET("materials") // Altere o endpoint conforme a rota da sua API
    suspend fun getMaterials(): List<MaterialItem>
}