package com.example.Habitarium.data.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONObject

object SessionManager {
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext
                .getSharedPreferences("habitarium_session", Context.MODE_PRIVATE)
        }
    }

    val token: String? get() = prefs?.getString("token", null)
    val name: String get() = prefs?.getString("name", "").orEmpty()
    val email: String get() = prefs?.getString("email", "").orEmpty()

    fun isLoggedIn(): Boolean {
        val t = token ?: return false
        return System.currentTimeMillis() < expiryMillis(t)
    }

    fun save(token: String, name: String, email: String) {
        prefs?.edit()
            ?.putString("token", token)
            ?.putString("name", name)
            ?.putString("email", email)
            ?.apply()
    }

    fun clear() {
        prefs?.edit()?.clear()?.apply()
    }

    // Lê a data de expiração ("exp", em segundos) de dentro do próprio token.
    private fun expiryMillis(token: String): Long = try {
        val payload = token.split(".")[1]
        val json = String(
            Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP),
            Charsets.UTF_8
        )
        JSONObject(json).getLong("exp") * 1000
    } catch (e: Exception) {
        0L
    }
}