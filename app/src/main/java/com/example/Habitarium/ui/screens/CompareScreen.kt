package com.example.Habitarium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.Habitarium.ui.theme.*
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(navController: NavController, state: CatalogState) {
    // Produtos escolhidos na busca, compartilhados entre as telas.
    val products = state.comparedProducts

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comparação") },
                actions = {
                    AssistChip(
                        onClick = { navController.navigateToTab("search") },
                        label   = { Text("${products.size} produtos") },
                        colors  = AssistChipDefaults.assistChipColors(
                            containerColor = BrandGreenLight,
                            labelColor     = BrandGreenDark
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                }
            )
        }
    ) { padding ->

        if (products.isEmpty()) {
            Box(
                modifier          = Modifier.fillMaxSize().padding(padding),
                contentAlignment  = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚖️", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text      = "Nenhum produto selecionado para comparar",
                        style     = MaterialTheme.typography.bodyMedium,
                        color     = NeutralMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigateToTab("search") },
                        colors  = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                    ) {
                        Text("Ir para busca")
                    }
                }
            }
            return@Scaffold
        }

        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    products.forEach { product ->
                        CompareProductHeader(
                            name = product.name,
                            company = product.company,
                            brand = product.brand,
                            imageUrl = product.imageUrl,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                HorizontalDivider()
            }

            item {
                val minPrice = products.minOf { it.price }
                CompareAttributeRow(label = "Preço") {
                    products.forEach { p ->
                        CompareCell(
                            value     = currency(p.price),
                            highlight = p.price == minPrice
                        )
                    }
                }
            }

            item {
                val maxRating = products.maxOf { it.rating }
                CompareAttributeRow(label = "Avaliação") {
                    products.forEach { p ->
                        CompareCell(
                            value     = "★ ${"%.1f".format(p.rating)}",
                            highlight = p.rating == maxRating
                        )
                    }
                }
            }

            item {
                CompareAttributeRow(label = "Marca") {
                    products.forEach { p -> CompareCell(value = p.brand.ifBlank { "—" }) }
                }
            }
            item {
                CompareAttributeRow(label = "Instalação") {
                    products.forEach { p ->
                        CompareCell(value = if (p.hasOwnInstallation) "Disponível" else "Não")
                    }
                }
            }
            item {
                CompareAttributeRow(label = "Estoque") {
                    products.forEach { p ->
                        CompareCell(value = if (p.stock > 0) "${p.stock} un." else "—")
                    }
                }
            }
            items(products.flatMap { it.specs.keys }.distinct()) { attr ->
                CompareAttributeRow(label = specLabel(attr)) {
                    products.forEach { p -> CompareCell(value = p.specs[attr] ?: "—") }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    products.forEachIndexed { index, product ->
                        val isPrimary = index == products.indices.last
                        if (isPrimary) {
                            Button(
                                onClick  = { state.add(product); navController.navigateToTab("budget") },
                                modifier = Modifier.weight(1f),
                                colors   = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                            ) { Text("+ Orçamento", style = MaterialTheme.typography.labelSmall) }
                        } else {
                            OutlinedButton(
                                onClick  = { state.add(product); navController.navigateToTab("budget") },
                                modifier = Modifier.weight(1f)
                            ) { Text("+ Orçamento", style = MaterialTheme.typography.labelSmall, color = BrandGreen) }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CompareProductHeader(
    name: String,
    company: String,
    brand: String,
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(0.5.dp, NeutralBorder)
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BrandGreenLight)
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(BrandGreenLight),
                    error = ColorPainter(BrandGreenLight),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            minLines = 3,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (brand.isNotBlank()) brand else company,
            style = MaterialTheme.typography.bodySmall,
            color = NeutralMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CompareAttributeRow(label: String, cells: @Composable RowScope.() -> Unit) {
    Column {
        Text(
            text     = label,
            style    = MaterialTheme.typography.labelSmall,
            color    = NeutralMuted,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) { cells() }
        HorizontalDivider()
    }
}

@Composable
private fun RowScope.CompareCell(value: String, highlight: Boolean = false) {
    Box(
        modifier         = Modifier.weight(1f).border(0.5.dp, NeutralBorder).padding(10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text  = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (highlight) BrandGreen else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun specLabel(key: String) = when (key) {
    "unidadeMedida" -> "Unidade"
    "codigoProduto" -> "Código"
    "sistema" -> "Sistema"
    else -> key.replaceFirstChar { it.uppercase() }
}
