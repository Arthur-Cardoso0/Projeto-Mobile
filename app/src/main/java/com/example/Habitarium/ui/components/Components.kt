package com.example.Habitarium.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import com.example.Habitarium.ui.screens.currency
import com.example.Habitarium.ui.theme.BrandGreen
import com.example.Habitarium.ui.theme.NeutralMuted
import com.example.Habitarium.ui.theme.SponsoredAmber
import com.example.Habitarium.ui.theme.SponsoredAmberDark
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import com.example.Habitarium.ui.theme.BrandGreenDark
import com.example.Habitarium.ui.theme.BrandGreenLight

@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier
    )
}

@Composable
private fun SponsoredBadge() {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = SponsoredAmber
    ) {
        Text(
            text = "Patrocinado",
            style = MaterialTheme.typography.labelSmall,
            color = SponsoredAmberDark,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ProductCard(
    name: String,
    price: Double,
    company: String,
    isSponsored: Boolean,
    onAddToBudget: () -> Unit,
    imageUrl: String? = null,
    brand: String = "",
    rating: Float = 0f,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(BrandGreenLight),
                    error = ColorPainter(BrandGreenLight),
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
            } else {
                Box(Modifier.fillMaxWidth().height(150.dp).background(BrandGreenLight))
            }
            if (isSponsored) {
                Box(Modifier.padding(8.dp)) { SponsoredBadge() }
            }
        }
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (brand.isNotBlank()) brand else company,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (rating > 0f) {
                Text(
                    text = "★ ${"%.1f".format(rating)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = SponsoredAmberDark
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = currency(price),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = BrandGreenDark
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onAddToBudget,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text("+ Orçamento")
            }
        }
    }
}

@Composable
fun SearchResultRow(
    name: String,
    price: Double,
    company: String,
    brand: String,
    rating: Float,
    reviewCount: Int,
    isSponsored: Boolean,
    isCompared: Boolean,
    canCompare: Boolean,
    onToggleCompare: () -> Unit,
    onAdd: () -> Unit,
    imageUrl: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            if (isCompared) BrandGreen else MaterialTheme.colorScheme.outline
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        placeholder = ColorPainter(BrandGreenLight),
                        error = ColorPainter(BrandGreenLight),
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Box(
                        Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandGreenLight)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    if (isSponsored) {
                        SponsoredBadge()
                        Spacer(Modifier.height(4.dp))
                    }
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (brand.isNotBlank()) "$brand · $company" else company,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (rating > 0f) {
                        Text(
                            text = "★ ${"%.1f".format(rating)} ($reviewCount)",
                            style = MaterialTheme.typography.labelMedium,
                            color = SponsoredAmberDark
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = currency(price),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreenDark
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = isCompared,
                    onClick = onToggleCompare,
                    enabled = isCompared || canCompare,
                    label = { Text(if (isCompared) "Comparando" else "Comparar") }
                )
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text("Adicionar")
                }
            }
        }
    }
}