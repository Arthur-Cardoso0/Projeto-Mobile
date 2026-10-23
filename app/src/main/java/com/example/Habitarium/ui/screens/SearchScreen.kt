package com.example.Habitarium.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
    val results = searchProducts(query, category, ascending)
    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(value = query, onValueChange = { query = it },
            label = { Text("Buscar materiais") }, singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("Todos", "Drywall", "Steel Frame", "Forros")) { label ->
                CategoryChip(label, category == label, { category = label })
            }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${results.size} resultados")
            TextButton(onClick = { ascending = !ascending }) { Text(if (ascending) "Menor preço" else "Maior preço") }
        }
        OutlinedButton(onClick = { navController.navigate(Screen.Compare.route) }, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Comparar (${state.comparedProducts.size}/3)")
        }
        LazyColumn(Modifier.weight(1f)) {
            if (results.isEmpty()) item { Text("Nenhum produto encontrado. Tente outra busca ou categoria.", Modifier.padding(16.dp)) }
            items(results, key = { it.id }) { product ->
                SearchResultRow(name = product.name, price = product.price, company = product.company,
                    rating = product.rating, isSponsored = product.isSponsored,
                    onAdd = { state.add(product); navController.navigate(Screen.Budget.route) })
                Row(Modifier.padding(horizontal = 16.dp)) {
                    FilterChip(selected = state.isCompared(product.id),
                        onClick = { state.toggleComparison(product.id) },
                        enabled = state.isCompared(product.id) || state.comparedProducts.size < 3,
                        label = { Text(if (state.isCompared(product.id)) "Selecionado para comparar" else "Comparar produto") })
                }
                HorizontalDivider()
            }
        }
    }
}

