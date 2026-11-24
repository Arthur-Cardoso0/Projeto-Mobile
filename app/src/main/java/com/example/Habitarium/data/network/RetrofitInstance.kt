package com.example.Habitarium.data.network

import com.example.Habitarium.data.auth.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    // Com "adb reverse tcp:5240 tcp:5240", o localhost do aparelho aponta para o PC.
    private const val BASE_URL = "http://localhost:5240/api/"

    // Coloca "Authorization: Bearer <token>" em toda requisição, se houver login.
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

    // BASIC mostra só "--> POST url" e "<-- 200", sem expor senha nem token no Logcat.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
        redactHeader("Authorization")
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
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