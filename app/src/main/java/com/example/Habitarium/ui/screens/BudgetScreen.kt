package com.example.Habitarium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.content.Intent
import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.Habitarium.ui.theme.*


private data class BudgetRowData(
    val id: String,
    val name: String,
    val company: String,
    val quantity: Int,
    val unitPrice: Double,
    val isService: Boolean = false
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(navController: NavController, state: CatalogState) {
    // Estado compartilhado e salvo localmente.
    val items = state.budgetProducts.map { BudgetRowData(it.id, it.name, it.company, state.quantity(it.id), it.price) }
    val context = LocalContext.current
    val shareBudget: () -> Unit = {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Solicitação de orçamento")
            putExtra(Intent.EXTRA_TEXT, state.budgetText())
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Enviar orçamento"))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "Nenhum aplicativo disponível para compartilhar.", Toast.LENGTH_LONG).show()
        }
    }

    val materialsTotal = items.filter { !it.isService }.sumOf { it.unitPrice * it.quantity }
    val servicesTotal  = items.filter { it.isService  }.sumOf { it.unitPrice * it.quantity }
    val grandTotal     = materialsTotal + servicesTotal

    if (items.isEmpty()) {
        BudgetEmptyState(onSearchClick = { navController.navigate("search") })
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BudgetHeader(
            total         = grandTotal,
            itemCount     = items.count { !it.isService },
            serviceCount  = items.count { it.isService },
            onShareClick  = shareBudget
        )

        LazyColumn(modifier = Modifier.weight(1f)) {

            val materials = items.filter { !it.isService }
            if (materials.isNotEmpty()) {
                item { SectionLabel("Materiais") }
                items(materials) { item ->
                    BudgetRow(
                        item     = item,
                        onRemove = { state.remove(item.id) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }

            val services = items.filter { it.isService }
            if (services.isNotEmpty()) {
                item { SectionLabel("Serviços") }
                items(services) { item ->
                    ServiceRow(
                        item     = item,
                        onRemove = { state.remove(item.id) }
                    )
                }
            }

            item {
                TotalsSummary(
                    materialsTotal = materialsTotal,
                    servicesTotal  = servicesTotal,
                    grandTotal     = grandTotal
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Valores de demonstração. Escolha um aplicativo e destinatário para enviar a solicitação.", style = MaterialTheme.typography.bodySmall)
            Button(
                onClick  = shareBudget,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Icon(Icons.Outlined.Send, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Enviar solicitação", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}


@Composable
private fun BudgetHeader(
    total: Double,
    itemCount: Int,
    serviceCount: Int,
    onShareClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandGreen)
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top
        ) {
            Column {
                Text(
                    text  = "Meu orçamento",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text  = currency(total),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Text(
                    text  = "$itemCount ${ if (itemCount == 1) "material" else "materiais" } · " +
                            "$serviceCount ${ if (serviceCount == 1) "serviço" else "serviços" }",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
            IconButton(onClick = onShareClick) {
                Icon(Icons.Outlined.Share, contentDescription = "Compartilhar", tint = Color.White)
            }
        }
    }
}


@Composable
private fun SectionLabel(text: String) {
    Text(
        text     = text.uppercase(),
        style    = MaterialTheme.typography.labelSmall,
        color    = NeutralMuted,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
    )
}


@Composable
private fun BudgetRow(item: BudgetRowData, onRemove: () -> Unit) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.bodyMedium)
            Text(
                text  = "${item.company} · ${item.quantity} un",
                style = MaterialTheme.typography.bodySmall,
                color = NeutralMuted
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text  = currency(item.unitPrice * item.quantity),
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Remover",
                    tint     = NeutralMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ServiceRow(item: BudgetRowData, onRemove: () -> Unit) {
    Surface(
        shape    = RoundedCornerShape(12.dp),
        color    = BrandGreenLight,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier              = Modifier.padding(12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Outlined.Build, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.bodyMedium, color = BrandGreenDark)
                Text(
                    text  = "${item.company} · R$ ${"%.2f".format(item.unitPrice)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandGreen
                )
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Outlined.Delete, contentDescription = "Remover", tint = BrandGreenMid, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun TotalsSummary(materialsTotal: Double, servicesTotal: Double, grandTotal: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))
        TotalLine("Materiais",  materialsTotal)
        TotalLine("Serviços",   servicesTotal)
        Spacer(Modifier.height(4.dp))
        TotalLine("Total geral", grandTotal, isGrand = true)
    }
}

@Composable
private fun TotalLine(label: String, value: Double, isGrand: Boolean = false) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text  = label,
            style = if (isGrand) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = if (isGrand) MaterialTheme.colorScheme.onSurface else NeutralMuted
        )
        Text(
            text  = currency(value),
            style = if (isGrand) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = if (isGrand) BrandGreen else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BudgetEmptyState(onSearchClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("🧾", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))
            Text(
                text      = "Seu orçamento está vazio",
                style     = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text      = "Busque materiais e adicione ao orçamento para continuar",
                style     = MaterialTheme.typography.bodySmall,
                color     = NeutralMuted,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onSearchClick,
                colors  = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text("Buscar materiais")
            }
        }
    }
}

