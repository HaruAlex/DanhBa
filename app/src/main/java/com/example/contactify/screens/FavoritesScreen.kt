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
fun FavoritesScreen(
    viewModel: ContactViewModel,
    onContactClick: (Contact) -> Unit,
    onEditContact: (Contact) -> Unit,
    onBack: () -> Unit
) {
    val context   = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val favorites by viewModel.favoriteContacts.observeAsState(emptyList())
    val allGroups by viewModel.allGroups.observeAsState(emptyList())

    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var showContextMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⭐ Yêu thích (${favorites.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.StarBorder,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Chưa có liên hệ yêu thích", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(items = favorites, key = { it.id }) { contact ->
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
                                onEdit        = { onEditContact(contact) },
                                onFavorite    = { viewModel.toggleFavorite(contact) },
                                onCopy = {
                                    clipboard.setText(AnnotatedString(contact.phone))
                                    Toast.makeText(context, "Đã sao chép số điện thoại", Toast.LENGTH_SHORT).show()
                                },
                                onMoveToGroup = { /* xử lý trong ContactScreen */ },
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
