package com.example.Habitarium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.Habitarium.ui.theme.BrandGreenDark
import com.example.Habitarium.ui.theme.BrandGreenLight

@Composable
fun AccountScreen(auth: AuthState) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Box(
            Modifier.size(80.dp).clip(CircleShape).background(BrandGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                auth.userName.take(1).uppercase().ifEmpty { "?" },
                style = MaterialTheme.typography.headlineMedium,
                color = BrandGreenDark
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(auth.userName, style = MaterialTheme.typography.titleLarge)
        Text(
            auth.userEmail,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "Seu orçamento é salvo neste aparelho.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.weight(1f))
        OutlinedButton(
            onClick = { auth.logout() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Sair", style = MaterialTheme.typography.titleMedium)
        }
    }
}