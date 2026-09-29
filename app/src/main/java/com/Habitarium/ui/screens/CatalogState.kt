package com.example.Habitarium.ui.screens

import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.Habitarium.ui.theme.Product
import org.json.JSONObject

class CatalogState(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences("habitarium_catalog", 0)
    private val quantities = mutableStateMapOf<String, Int>()
    private val selectedIds = mutableStateListOf<String>()
    val budgetProducts get() = catalog.filter { (quantities[it.id] ?: 0) > 0 }
    val comparedProducts get() = catalog.filter { it.id in selectedIds }

    init {
        val saved = runCatching { JSONObject(preferences.getString("quantities", "{}") ?: "{}") }.getOrDefault(JSONObject())
        catalog.forEach { product ->
            val quantity = saved.optInt(product.id, 0).coerceIn(0, 999)
            if (quantity > 0) quantities[product.id] = quantity
        }
        val selected = preferences.getStringSet("comparison", emptySet()).orEmpty()
        selectedIds.addAll(catalog.filter { it.id in selected }.take(3).map { it.id })
    }

    fun quantity(id: String) = quantities[id] ?: 0
    fun add(product: Product) {
        quantities[product.id] = (quantity(product.id) + 1).coerceAtMost(999)
        persist()
    }
    fun remove(id: String) { quantities.remove(id); persist() }
    fun isCompared(id: String) = id in selectedIds
    fun toggleComparison(id: String) {
        if (id in selectedIds) selectedIds.remove(id)
        else if (selectedIds.size < 3) selectedIds.add(id)
        persist()
    }
    fun budgetText(): String = buildString {
        appendLine("Solicitação de orçamento — Habitarium")
        budgetProducts.forEach { appendLine("${quantity(it.id)} × ${it.name}: ${currency(it.price * quantity(it.id))}") }
        appendLine("Total estimado: ${currency(budgetProducts.sumOf { it.price * quantity(it.id) })}")
        append("Valores de demonstração, sujeitos à confirmação do fornecedor.")
    }
    private fun persist() {
        val json = JSONObject()
        quantities.forEach { (id, quantity) -> json.put(id, quantity) }
        preferences.edit().putString("quantities", json.toString())
            .putStringSet("comparison", selectedIds.toSet()).apply()
    }
}


