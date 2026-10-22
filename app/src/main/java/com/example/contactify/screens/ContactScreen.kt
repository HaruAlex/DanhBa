package com.example.contactify.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.contactify.components.ContactContextMenu
import com.example.contactify.components.ContactDropdownMenu
import com.example.contactify.components.ContactItem
import com.example.contactify.data.Contact
import com.example.contactify.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    viewModel: ContactViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onContactClick: (Contact) -> Unit,
    onEditContact: (Contact) -> Unit,
    onAddContact: () -> Unit,
    onSearchClick: () -> Unit,
    onGroupClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onApiUsersClick: () -> Unit
) {
    val context        = LocalContext.current
    val clipboard      = LocalClipboardManager.current
    val contacts       by viewModel.contacts.observeAsState(emptyList())
    val allGroups      by viewModel.allGroups.observeAsState(emptyList())

    var showTopMenu       by remember { mutableStateOf(false) }
    var selectedContact   by remember { mutableStateOf<Contact?>(null) }
    var showContextMenu   by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showGroupDialog   by remember { mutableStateOf(false) }

    val isMultiSelect = viewModel.isMultiSelectMode
    val selectedIds   = viewModel.selectedIds

    // ── Dialog xác nhận xóa nhiều ─────────────────────────
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xác nhận xóa") },
            text  = { Text("Bạn có chắc muốn xóa ${selectedIds.size} liên hệ đã chọn?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSelectedContacts()
                    showDeleteConfirm = false
                    Toast.makeText(context, "Đã xóa ${selectedIds.size} liên hệ", Toast.LENGTH_SHORT).show()
                }) { Text("Xóa", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Hủy") }
            }
        )
    }

    // ── Dialog đổi nhóm ──────────────────────────────────
    if (showGroupDialog && selectedContact != null) {
        var chosenGroup by remember { mutableStateOf(selectedContact!!.group) }
        AlertDialog(
            onDismissRequest = { showGroupDialog = false },
            title = { Text("Chuyển sang nhóm") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    allGroups.forEach { g ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = chosenGroup == g,
                                onClick  = { chosenGroup = g }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(g)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedContact?.let { c ->
                        viewModel.updateContact(
                            id = c.id, name = c.name, phone = c.phone,
                            email = c.email, group = chosenGroup
                        )
                    }
                    showGroupDialog = false
                    selectedContact = null
                }) { Text("Lưu") }
            },
            dismissButton = {
                TextButton(onClick = { showGroupDialog = false }) { Text("Hủy") }
            }
        )
    }

    Scaffold(
        topBar = {
            if (isMultiSelect) {
                // ── TopBar chế độ chọn nhiều ─────────────────
                TopAppBar(
                    title = { Text("Đã chọn: ${selectedIds.size}") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearMultiSelect() }) {
                            Icon(Icons.Default.Close, contentDescription = "Bỏ chọn")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.selectAll(contacts) }) {
                            Icon(Icons.Default.SelectAll, contentDescription = "Chọn tất cả")
                        }
                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            enabled = selectedIds.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Xóa đã chọn",
                                tint = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                )
            } else {
                // ── TopBar bình thường ───────────────────────
                TopAppBar(
                    title = { Text("Danh bạ") },
                    actions = {
                        Box {
                            IconButton(onClick = { showTopMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                            }
                            ContactDropdownMenu(
                                expanded          = showTopMenu,
                                isDarkMode        = isDarkMode,
                                onDismiss         = { showTopMenu = false },
                                onSearch          = { onSearchClick() },
                                onGroup           = { onGroupClick() },
                                onFavorites       = { onFavoritesClick() },
                                onStatistics      = { onStatisticsClick() },
                                onApiUsers        = { onApiUsersClick() },
                                onTheme           = { onToggleDarkMode() },
                                onExit            = { (context as? android.app.Activity)?.finish() }
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!isMultiSelect) {
                FloatingActionButton(onClick = onAddContact) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm liên hệ")
                }
            }
        }
    ) { padding ->

        if (contacts.isEmpty()) {
            // ── Empty state ──────────────────────────────
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PersonOff,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Chưa có liên hệ nào", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onAddContact) { Text("Thêm liên hệ mới") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(items = contacts, key = { it.id }) { contact ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ContactItem(
                            contact      = contact,
                            isSelected   = selectedIds.contains(contact.id),
                            isMultiSelect = isMultiSelect,
                            onClick = {
                                if (isMultiSelect) {
                                    viewModel.toggleSelection(contact.id)
                                } else {
                                    onContactClick(contact)
                                }
                            },
                            onLongClick = {
                                if (!isMultiSelect) {
                                    selectedContact = contact
                                    showContextMenu = true
                                }
                            }
                        )

                        if (showContextMenu && selectedContact?.id == contact.id) {
                            ContactContextMenu(
                                expanded   = true,
                                contact    = contact,
                                allGroups  = allGroups,
                                onDismiss  = { showContextMenu = false; selectedContact = null },
                                onCall = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                                    context.startActivity(intent)
                                },
                                onEdit     = { onEditContact(contact) },
                                onFavorite = { viewModel.toggleFavorite(contact) },
                                onCopy = {
                                    clipboard.setText(AnnotatedString(contact.phone))
                                    Toast.makeText(context, "Đã sao chép số điện thoại", Toast.LENGTH_SHORT).show()
                                },
                                onMoveToGroup = {
                                    selectedContact = contact
                                    showGroupDialog = true
                                },
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
