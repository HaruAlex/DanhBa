package com.example.contactify.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contactify.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: ContactViewModel,
    onBack: () -> Unit
) {
    val total     by viewModel.totalCount.observeAsState(0)
    val favorites by viewModel.favoriteCount.observeAsState(0)
    val groups    by viewModel.allGroups.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thống kê danh bạ") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Tổng quan ─────────────────────────────────
            item {
                Text("Tổng quan", style = MaterialTheme.typography.titleMedium)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "📱 Tổng liên hệ",
                        value = total.toString()
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "⭐ Yêu thích",
                        value = favorites.toString()
                    )
                }
            }

            // ── Theo nhóm ─────────────────────────────────
            item {
                Spacer(Modifier.height(4.dp))
                Text("👥 Theo nhóm", style = MaterialTheme.typography.titleMedium)
            }

            items(groups) { group ->
                val count by viewModel.getGroupCount(group).observeAsState(0)
                val percent = if (total > 0) (count * 100f / total) else 0f
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(group, style = MaterialTheme.typography.bodyLarge)
                            Text("$count liên hệ", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress    = { percent / 100f },
                            modifier    = Modifier.fillMaxWidth(),
                            trackColor  = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "%.1f%%".format(percent),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}
