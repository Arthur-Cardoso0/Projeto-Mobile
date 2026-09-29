package com.example.Habitarium.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Delete
import com.example.Habitarium.ui.screens.currency
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.Habitarium.ui.theme.*

@Composable
fun SponsoredBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SponsoredAmber)
            .border(0.5.dp, SponsoredAmberBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text  = "Patrocinado",
            style = MaterialTheme.typography.labelSmall,
            color = SponsoredAmberDark
        )
    }
}

@Composable
fun ProductCard(
    name: String,
    price: Double,
    company: String,
    isSponsored: Boolean = false,
    onAddToBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.width(160.dp),
        shape     = RoundedCornerShape(10.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border    = androidx.compose.foundation.BorderStroke(0.5.dp, NeutralBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            if (isSponsored) {
                SponsoredBadge()
                Spacer(Modifier.height(4.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NeutralSurface),
                contentAlignment = Alignment.Center
            ) {
                Text("📦", style = MaterialTheme.typography.headlineMedium)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text     = name,
                style    = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text  = currency(price),
                style = MaterialTheme.typography.titleMedium,
                color = BrandGreen
            )

            Text(
                text  = company,
                style = MaterialTheme.typography.bodySmall,
                color = NeutralMuted
            )

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick  = onAddToBudget,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddCircleOutline,
                    contentDescription = null,
                    tint   = BrandGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("Adicionar", style = MaterialTheme.typography.labelSmall, color = BrandGreen)
            }
        }
    }
}

@Composable
fun SearchResultRow(
    name: String,
    price: Double,
    company: String,
    rating: Float,
    isSponsored: Boolean = false,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(NeutralSurface),
            contentAlignment = Alignment.Center
        ) {
            Text("📦")
        }

        Column(modifier = Modifier.weight(1f)) {
            if (isSponsored) {
                SponsoredBadge()
                Spacer(Modifier.height(2.dp))
            }
            Text(name, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(company, style = MaterialTheme.typography.bodySmall, color = NeutralMuted)
                Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFBA7517), modifier = Modifier.size(12.dp))
                Text("${"%.1f".format(rating)}", style = MaterialTheme.typography.bodySmall, color = NeutralMuted)
            }
            Text(currency(price), style = MaterialTheme.typography.titleMedium, color = BrandGreen)
        }

        IconButton(onClick = onAdd) {
            Icon(Icons.Outlined.AddCircleOutline, contentDescription = "Adicionar ao orçamento", tint = BrandGreen)
        }
    }
}

@Composable
fun CategoryChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick  = onClick,
        label    = { Text(label, style = MaterialTheme.typography.labelSmall) },
        colors   = FilterChipDefaults.filterChipColors(
            selectedContainerColor    = BrandGreenLight,
            selectedLabelColor        = BrandGreenDark,
            selectedLeadingIconColor  = BrandGreenDark
        )
    )
}

@Composable
fun BudgetItemRow(
    name: String,
    meta: String,
    totalPrice: Double,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.bodyMedium)
            Text(meta, style = MaterialTheme.typography.bodySmall, color = NeutralMuted)
        }
        Text(currency(totalPrice), style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.width(12.dp))
        IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Remover item",
                tint = NeutralMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
