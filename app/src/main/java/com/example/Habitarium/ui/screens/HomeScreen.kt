package com.example.Habitarium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.Habitarium.ui.components.CategoryChip
import com.example.Habitarium.ui.components.ProductCard

@Composable
fun HomeScreen(navController: NavController, state: CatalogState) {
    var category by rememberSaveable { mutableStateOf("Todos") }
    val products = searchProducts("", category, true)
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState())) {
        Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(16.dp)) {
            Text("Habitarium", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary)
            Text("Materiais para sua obra", color = MaterialTheme.colorScheme.onPrimary)
            Spacer(Modifier.height(12.dp))
            FilledTonalButton(onClick = { navController.navigate(Screen.Search.route) }) {
                Text("Buscar materiais e serviços")
            }
        }
        Text("Catálogo de demonstração", Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall)
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("Todos", "Drywall", "Steel Frame", "Forros")) { label ->
                CategoryChip(label, category == label, { category = label })
            }
        }
        Text("Destaques", Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
        if (products.isEmpty()) Text("Nenhum produto nesta categoria.", Modifier.padding(16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(products, key = { it.id }) { product ->
                ProductCard(name = product.name, price = product.price, company = product.company,
                    isSponsored = product.isSponsored, onAddToBudget = {
                        state.add(product)
                        navController.navigate(Screen.Budget.route)
                    })
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

