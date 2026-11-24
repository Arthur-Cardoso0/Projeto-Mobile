package com.example.Habitarium.data.network

import com.example.Habitarium.data.auth.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    // URL da API no Render mantendo o sufixo "/api/" utilizado pelas rotas do HabitariumApiService
    private const val BASE_URL = "https://api-habitarium-biho.onrender.com/api/"

    // Injeta "Authorization: Bearer <token>" em todas as requisições quando houver sessão ativa
    private val authInterceptor = Interceptor { chain ->
        val token = SessionManager.token
        val request = if (token != null) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    // Registra apenas método, URL e status HTTP no Logcat, ocultando o cabeçalho Authorization
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
        redactHeader("Authorization")
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: HabitariumApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HabitariumApiService::class.java)
    }
}