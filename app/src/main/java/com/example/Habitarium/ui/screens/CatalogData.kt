package com.example.Habitarium.ui.screens

import com.example.Habitarium.ui.theme.Product
import java.text.Normalizer
import java.text.NumberFormat
import java.util.Locale

val catalog = listOf(
    Product("placa-10", "Placa Cimentícia 10mm 1200×2400", 89.90, "Espaço Smart", "Steel Frame", "Smart", specs = mapOf("Espessura" to "10 mm", "Área" to "2,88 m²", "Estoque" to "Disponível"), stock = 20, rating = 4.2f, isSponsored = true),
    Product("eternit-10", "Placa Cimentícia Eternit 10mm", 94.50, "Espaço Smart", "Steel Frame", "Eternit", specs = mapOf("Espessura" to "10 mm", "Área" to "2,88 m²", "Estoque" to "Disponível"), stock = 15, rating = 4.8f),
    Product("eternit-15", "Placa Cimentícia Eternit 15mm", 118.00, "Espaço Smart", "Steel Frame", "Eternit", specs = mapOf("Espessura" to "15 mm", "Área" to "2,88 m²", "Estoque" to "Disponível"), stock = 12, rating = 4.5f),
    Product("gesso", "Placa de Gesso Performa RU BR 12,5mm", 62.00, "Espaço Smart", "Drywall", "Performa", stock = 30, rating = 4.1f),
    Product("montante", "Montante Drywall 70mm Z120", 18.50, "Espaço Smart", "Drywall", "Smart", stock = 50, rating = 4.3f),
    Product("fita", "Fita Banda Acústica 70×10m", 41.00, "Espaço Smart", "Drywall", "Smart", stock = 25, rating = 4.0f)
)

fun currency(value: Double): String = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(value)

private fun String.searchKey() = Normalizer.normalize(trim(), Normalizer.Form.NFD)
    .replace("\\p{M}+".toRegex(), "").lowercase(Locale.ROOT)

fun searchProducts(query: String, category: String, ascending: Boolean): List<Product> {
    val terms = query.searchKey().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    val matches = catalog.filter { product ->
        val text = "${product.name} ${product.company} ${product.brand}".searchKey()
        (category == "Todos" || product.category == category) && terms.all { it in text }
    }
    return if (ascending) matches.sortedBy { it.price } else matches.sortedByDescending { it.price }
}


