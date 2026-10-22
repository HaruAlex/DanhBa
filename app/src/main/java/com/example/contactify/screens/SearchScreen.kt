package com.example.contactify.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.contactify.components.ContactContextMenu
import com.example.contactify.components.ContactItem
import com.example.contactify.data.Contact
import com.example.contactify.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: ContactViewModel,
    onContactClick: (Contact) -> Unit,
    onBack: () -> Unit
) {
    val context   = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val query     by viewModel.searchQuery.observeAsState("")
    val contacts  by viewModel.contacts.observeAsState(emptyList())
    val allGroups by viewModel.allGroups.observeAsState(emptyList())
    val focusRequester = remember { FocusRequester() }

    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var showContextMenu by remember { mutableStateOf(false) }

    // Auto-focus ô tìm kiếm khi vào màn hình
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tìm kiếm") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearSearchQuery()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            // ── Ô tìm kiếm ───────────────────────────────
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.setSearchQuery(it) },
                label = { Text("Nhập tên hoặc số điện thoại…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .focusRequester(focusRequester),
                singleLine = true,
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearSearchQuery() }) {
                            Icon(Icons.Default.Close, contentDescription = "Xóa")
                        }
                    }
                }
            )

            // ── Kết quả ───────────────────────────────────
            if (query.isNotBlank() && contacts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Không tìm thấy kết quả cho \"$query\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = contacts, key = { it.id }) { contact ->
                        Box(Modifier.fillMaxWidth()) {
                            ContactItem(
                                contact = contact,
                                isSelected = false,
                                isMultiSelect = false,
                                onClick    = { onContactClick(contact) },
                                onLongClick = {
                                    selectedContact = contact
                                    showContextMenu = true
                                }
                            )
                            if (showContextMenu && selectedContact?.id == contact.id) {
                                ContactContextMenu(
                                    expanded  = true,
                                    contact   = contact,
                                    allGroups = allGroups,
                                    onDismiss = { showContextMenu = false; selectedContact = null },
                                    onCall = {
                                        context.startActivity(
                                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                                        )
                                    },
                                    onEdit     = { /* TODO navigate to edit */ },
                                    onFavorite = { viewModel.toggleFavorite(contact) },
                                    onCopy = {
                                        clipboard.setText(AnnotatedString(contact.phone))
                                        Toast.makeText(context, "Đã sao chép", Toast.LENGTH_SHORT).show()
                                    },
                                    onMoveToGroup = { /* TODO */ },
                                    onDelete = {
                                        viewModel.deleteContact(contact)
                                        Toast.makeText(context, "Đã xóa ${contact.name}", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
