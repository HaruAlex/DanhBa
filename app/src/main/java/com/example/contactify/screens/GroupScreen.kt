package com.example.contactify.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
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
fun GroupScreen(
    viewModel: ContactViewModel,
    onContactClick: (Contact) -> Unit,
    onEditContact: (Contact) -> Unit,
    onBack: () -> Unit
) {
    val context   = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val allGroups by viewModel.allGroups.observeAsState(emptyList())

    var selectedGroup   by remember { mutableStateOf<String?>(null) }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var showContextMenu by remember { mutableStateOf(false) }

    val groupContacts by remember(selectedGroup) {
        derivedStateOf {
            if (selectedGroup == null) emptyList()
            else viewModel.contacts.value?.filter { it.group == selectedGroup } ?: emptyList()
        }
    }

    // Lấy contacts theo group qua LiveData
    val contactsInGroup = selectedGroup?.let { g ->
        viewModel.getContactsByGroup(g).observeAsState(emptyList()).value
    } ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (selectedGroup == null) "Nhóm liên hệ" else "Nhóm: $selectedGroup")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedGroup != null) selectedGroup = null
                        else onBack()
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectedGroup == null) {
                // ── Danh sách nhóm ────────────────────────
                Text("Chọn một nhóm:", style = MaterialTheme.typography.titleSmall)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allGroups) { group ->
                        val count by viewModel.getGroupCount(group).observeAsState(0)
                        OutlinedCard(
                            onClick = { selectedGroup = group },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(group, style = MaterialTheme.typography.bodyLarge)
                                Badge { Text(count.toString()) }
                            }
                        }
                    }
                }
            } else {
                // ── Contacts trong nhóm ───────────────────
                Text(
                    "${contactsInGroup.size} liên hệ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = contactsInGroup, key = { it.id }) { contact ->
                        Box(Modifier.fillMaxWidth()) {
                            ContactItem(
                                contact = contact,
                                isSelected = false,
                                isMultiSelect = false,
                                onClick = { onContactClick(contact) },
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
                                    onEdit     = { onEditContact(contact) },
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
