package com.example.Habitarium.ui.screens

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Habitarium.data.auth.SessionManager
import com.example.Habitarium.data.model.AuthResponse
import com.example.Habitarium.data.model.LoginRequest
import com.example.Habitarium.data.model.RegisterRequest
import com.example.Habitarium.data.network.RetrofitInstance
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class AuthState(application: Application) : AndroidViewModel(application) {

    var isLoggedIn by mutableStateOf(SessionManager.isLoggedIn())
        private set
    var userName by mutableStateOf(SessionManager.name)
        private set
    var userEmail by mutableStateOf(SessionManager.email)
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun login(email: String, password: String) = authenticate {
        RetrofitInstance.api.login(LoginRequest(email.trim(), password))
    }

    fun register(name: String, email: String, password: String) = authenticate {
        RetrofitInstance.api.register(RegisterRequest(name.trim(), email.trim(), password))
    }

    fun logout() {
        SessionManager.clear()
        userName = ""
        userEmail = ""
        isLoggedIn = false
    }

    fun clearError() {
        error = null
    }

    private fun authenticate(call: suspend () -> AuthResponse) {
        if (loading) return
        loading = true
        error = null
        viewModelScope.launch {
            try {
                val response = call()
                SessionManager.save(response.token, response.name, response.email)
                userName = response.name
                userEmail = response.email
                isLoggedIn = true
            } catch (e: HttpException) {
                error = messageFor(e)
            } catch (e: IOException) {
                error = "Não foi possível conectar ao servidor."
            } finally {
                loading = false
            }
        }
    }

    private fun messageFor(e: HttpException): String {
        val fromServer = try {
            JSONObject(e.response()?.errorBody()?.string().orEmpty()).optString("message")
        } catch (_: Exception) {
            ""
        }
        return when {
            fromServer.isNotBlank() -> fromServer
            e.code() == 400 -> "Dados inválidos. Confira os campos."
            e.code() == 401 -> "E-mail ou senha inválidos."
            else -> "Erro no servidor (${e.code()})."
        }
    }
}