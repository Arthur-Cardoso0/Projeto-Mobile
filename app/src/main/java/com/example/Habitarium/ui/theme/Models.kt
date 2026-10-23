package com.example.Habitarium.ui.theme

// ─── Modelos de domínio (estrutura base para implementação) ──────────────────
// Expanda estes data classes conforme o dicionário de dados do projeto.

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val company: String,
    val category: String,
    val brand: String,
    val specs: Map<String, String> = emptyMap(), // ex: "Espessura" -> "10mm"
    val stock: Int = 0,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val isSponsored: Boolean = false,
    val hasOwnInstallation: Boolean = false,
    val imageUrl: String? = null
)

data class ServiceProvider(
    val id: String,
    val name: String,
    val specialty: String,  // ex: "Instalação de Drywall"
    val rating: Float = 0f,
    val priceEstimate: Double = 0.0
)

data class BudgetItem(
    val product: Product,
    val quantity: Int,
    val serviceProvider: ServiceProvider? = null
)

data class Budget(
    val id: String,
    val items: List<BudgetItem> = emptyList()
) {
    val materialsTotal: Double get() = items.sumOf { it.product.price * it.quantity }
    val servicesTotal: Double  get() = items.sumOf { it.serviceProvider?.priceEstimate ?: 0.0 }
    val total: Double          get() = materialsTotal + servicesTotal
}
