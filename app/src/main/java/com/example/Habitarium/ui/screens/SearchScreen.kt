package com.example.Habitarium.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.Habitarium.ui.components.CategoryChip
import com.example.Habitarium.ui.components.SearchResultRow

@Composable
fun SearchScreen(navController: NavController, state: CatalogState) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Todos") }
    var ascending by rememberSaveable { mutableStateOf(true) }
    val results = searchProducts(query, category, ascending, state.products)

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Buscar materiais, marcas ou empresas") },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
        when (val load = state.loadState) {
            is CatalogLoadState.Loading -> LinearProgressIndicator(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            is CatalogLoadState.Error -> Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(load.message, Modifier.weight(1f))
                TextButton(onClick = { state.loadCatalog() }) { Text("Tentar") }
            }
            else -> {}
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.categories) { label ->
                CategoryChip(label, category == label, { category = label })
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${results.size} resultados", style = MaterialTheme.typography.labelLarge)
            TextButton(onClick = { ascending = !ascending }) {
                Text(if (ascending) "Menor preço" else "Maior preço")
            }
        }
        FilledTonalButton(
            onClick = { navController.navigateToTab(Screen.Compare.route) },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("Comparar (${state.comparedProducts.size}/3)")
        }
        Spacer(Modifier.height(4.dp))
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = 16.dp)) {
            if (results.isEmpty()) {
                item {
                    Text(
                        "Nenhum produto encontrado. Tente outra busca ou categoria.",
                        Modifier.padding(16.dp)
                    )
                }
            }
            items(results, key = { it.id }) { product ->
                SearchResultRow(
                    name = product.name,
                    price = product.price,
                    company = product.company,
                    brand = product.brand,
                    rating = product.rating,
                    reviewCount = product.reviewCount,
                    isSponsored = product.isSponsored,
                    imageUrl = product.imageUrl,
                    isCompared = state.isCompared(product.id),
                    canCompare = state.comparedProducts.size < 3,
                    onToggleCompare = { state.toggleComparison(product.id) },
                    onAdd = {
                        state.add(product)
                        navController.navigateToTab(Screen.Budget.route)
                    }
                )
            }
        }
    }
}