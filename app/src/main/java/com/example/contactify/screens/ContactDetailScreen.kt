package com.example.contactify.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.contactify.data.Contact
import com.example.contactify.data.ContactWithPhones
import com.example.contactify.viewmodel.ContactViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contactId: Int,
    viewModel: ContactViewModel,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    // Tải dữ liệu theo id (suspend) khi màn hình mở
    var contactWithPhones by remember { mutableStateOf<ContactWithPhones?>(null) }

    LaunchedEffect(contactId) {
        contactWithPhones = viewModel.getContactWithPhonesById(contactId)
    }

    val cwp = contactWithPhones
    if (cwp == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val contact = cwp.contact
    val phones  = cwp.phones
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xác nhận xóa") },
            text  = { Text("Bạn có chắc muốn xóa liên hệ \"${contact.name}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteContact(contact)
                    showDeleteConfirm = false
                    onBack()
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
                title = { Text("Chi tiết liên hệ") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    // Yêu thích toggle
                    IconButton(onClick = { viewModel.toggleFavorite(contact) }) {
                        Icon(
                            imageVector = if (contact.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Yêu thích",
                            tint = if (contact.isFavorite) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Sửa
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Sửa")
                    }
                    // Xóa
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa",
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {

            // ── Avatar placeholder + Tên ──────────────────
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape     = MaterialTheme.shapes.extraLarge,
                        color     = MaterialTheme.colorScheme.primaryContainer,
                        modifier  = Modifier.size(88.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text  = contact.name.firstOrNull()?.uppercase() ?: "?",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(contact.name, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text  = contact.group,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Số điện thoại (tất cả) ────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Số điện thoại", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        if (phones.isEmpty()) {
                            Text(contact.phone.ifBlank { "Chưa có" },
                                style = MaterialTheme.typography.bodyMedium)
                        } else {
                            phones.forEach { phone ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(phone.number, style = MaterialTheme.typography.bodyMedium)
                                        Text(phone.label,  style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    // Nút gọi điện
                                    IconButton(onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone.number}"))
                                        context.startActivity(intent)
                                    }) {
                                        Icon(Icons.Default.Call, contentDescription = "Gọi điện",
                                            tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Email ─────────────────────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Email", style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                contact.email.ifBlank { "Chưa có" },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (contact.email.isNotBlank()) {
                            IconButton(onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${contact.email}"))
                                context.startActivity(intent)
                            }) {
                                Icon(Icons.Default.Email, contentDescription = "Gửi email",
                                    tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            // ── Nhóm & yêu thích ─────────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Nhóm", style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary)
                                Text(contact.group, style = MaterialTheme.typography.bodyMedium)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Yêu thích", style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary)
                                Icon(
                                    imageVector = if (contact.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (contact.isFavorite) MaterialTheme.colorScheme.primary
                                           else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
