package com.example.contactify.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contactify.viewmodel.ApiUiState
import com.example.contactify.viewmodel.ApiUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiUserDetailScreen(
    userId: Int,
    viewModel: ApiUserViewModel,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val uiState = viewModel.uiState
    val user    = viewModel.selectedUser

    // Tải chi tiết nếu chưa có
    LaunchedEffect(userId) {
        if (user?.id != userId) viewModel.loadUserById(userId)
    }

    if (showDeleteConfirm && user != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xác nhận xóa") },
            text  = { Text("Bạn có chắc muốn xóa user \"${user.name}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteUser(user.id) { onDeleted() }
                    showDeleteConfirm = false
                }) { Text("Xóa", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Hủy") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết người dùng") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Sửa")
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa",
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            when {
                uiState is ApiUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState is ApiUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(uiState.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadUserById(userId) }) { Text("Thử lại") }
                    }
                }

                user != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 20.dp)
                    ) {

                        // ── Avatar + Tên ──────────────────────────
                        item {
                            Column(
                                Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = MaterialTheme.shapes.extraLarge,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(88.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text  = user.name.firstOrNull()?.uppercase() ?: "?",
                                            style = MaterialTheme.typography.headlineLarge,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                Text(user.name, style = MaterialTheme.typography.headlineSmall)
                                Text("@${user.username}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // ── Liên hệ ───────────────────────────────
                        item {
                            DetailCard(title = "Liên hệ") {
                                DetailRow(label = "Email",   value = user.email)
                                DetailRow(label = "Điện thoại", value = user.phone)
                                DetailRow(label = "Website", value = user.website)
                            }
                        }

                        // ── Địa chỉ ───────────────────────────────
                        item {
                            DetailCard(title = "Địa chỉ") {
                                DetailRow(label = "Đường", value = "${user.address.street}, ${user.address.suite}")
                                DetailRow(label = "Thành phố", value = user.address.city)
                                DetailRow(label = "ZIP",    value = user.address.zipcode)
                            }
                        }

                        // ── Công ty ───────────────────────────────
                        item {
                            DetailCard(title = "Công ty") {
                                DetailRow(label = "Tên",        value = user.company.name)
                                DetailRow(label = "Slogan",     value = user.company.catchPhrase)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.35f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.65f)
        )
    }
}
