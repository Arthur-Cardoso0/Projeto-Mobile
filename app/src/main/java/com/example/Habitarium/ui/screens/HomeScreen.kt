package com.example.Habitarium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.Habitarium.ui.components.CategoryChip
import com.example.Habitarium.ui.components.ProductCard
import com.example.Habitarium.ui.theme.Product

@Composable
fun HomeScreen(navController: NavController, state: CatalogState) {
    var category by rememberSaveable { mutableStateOf("Todos") }
    val filtered = searchProducts("", category, true, state.products)
    val featured = filtered.sortedByDescending { it.isSponsored }.take(10)
    val topRated = filtered.sortedByDescending { it.rating }.take(10)

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Cabeçalho
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 20.dp)
        ) {
            Text("Habitarium", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary)
            Text("Materiais para sua obra", color = MaterialTheme.colorScheme.onPrimary)
            Spacer(Modifier.height(14.dp))
            Surface(
                onClick = { navController.navigateToTab(Screen.Search.route) },
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Buscar materiais, marcas ou empresas", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.categories) { label ->
                CategoryChip(label, category == label, { category = label })
            }
        }

        when (val load = state.loadState) {
            is CatalogLoadState.Loading -> Box(
                Modifier.fillMaxWidth().padding(48.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is CatalogLoadState.Error -> Column(
                Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(load.message)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { state.loadCatalog() }) { Text("Tentar novamente") }
            }

            is CatalogLoadState.Success -> {
                if (filtered.isEmpty()) {
                    Text("Nenhum produto nesta categoria.", Modifier.padding(16.dp))
                } else {
                    ProductSection("Destaques", featured, state, navController)
                    if (filtered.size > 3) {
                        ProductSection("Mais bem avaliados", topRated, state, navController)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ProductSection(
    title: String,
    products: List<Product>,
    state: CatalogState,
    navController: NavController
) {
    Text(
        title,
        Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp),
        style = MaterialTheme.typography.titleMedium
    )
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(products, key = { it.id }) { product ->
            ProductCard(
                name = product.name,
                price = product.price,
                company = product.company,
                isSponsored = product.isSponsored,
                imageUrl = product.imageUrl,
                brand = product.brand,
                rating = product.rating,
                onAddToBudget = {
                    state.add(product)
                    navController.navigateToTab(Screen.Budget.route)
                }
            )
        }
    }
}