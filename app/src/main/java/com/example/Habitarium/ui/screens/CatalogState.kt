package com.example.Habitarium.ui.screens

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Habitarium.data.model.toProduct
import com.example.Habitarium.data.network.RetrofitInstance
import com.example.Habitarium.ui.theme.Product
import com.google.gson.JsonParseException
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

sealed interface CatalogLoadState {
    data object Loading : CatalogLoadState
    data object Success : CatalogLoadState
    data class Error(val message: String) : CatalogLoadState
}

class CatalogState(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences("habitarium_catalog", 0)
    private val quantities = mutableStateMapOf<String, Int>()
    private val selectedIds = mutableStateListOf<String>()

    // Produtos vindos da API
    var products by mutableStateOf<List<Product>>(emptyList())
        private set
    var loadState by mutableStateOf<CatalogLoadState>(CatalogLoadState.Loading)
        private set

    val categories get() = listOf("Todos") + categoryGroups.filter { group ->
        products.any { categoryGroupOf(it.category) == group }
    }
    val budgetProducts get() = products.filter { (quantities[it.id] ?: 0) > 0 }
    val comparedProducts get() = products.filter { it.id in selectedIds }

    init {
        val saved = runCatching { JSONObject(preferences.getString("quantities", "{}") ?: "{}") }
            .getOrDefault(JSONObject())
        saved.keys().forEach { id ->
            val quantity = saved.optInt(id, 0).coerceIn(0, 999)
            if (quantity > 0) quantities[id] = quantity
        }
        selectedIds.addAll(preferences.getStringSet("comparison", emptySet()).orEmpty().take(3))
        loadCatalog()
    }

    fun loadCatalog() {
        loadState = CatalogLoadState.Loading
        viewModelScope.launch {
            loadState = try {
                val result = RetrofitInstance.api.getMaterials()
                android.util.Log.d("Catalog", "API respondeu com ${result.size} itens")
                products = result.map { it.toProduct() }
                selectedIds.retainAll(products.map { it.id }.toSet())
                persist()
                CatalogLoadState.Success
            } catch (e: Exception) {
                android.util.Log.e("Catalog", "Falha ao carregar: ${e::class.simpleName} - ${e.message}", e)
                CatalogLoadState.Error("Não foi possível carregar o catálogo.")
            }
        }
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