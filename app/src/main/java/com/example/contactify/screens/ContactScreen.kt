package com.example.contactify.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.example.contactify.components.ContactContextMenu
import com.example.contactify.components.ContactDropdownMenu
import com.example.contactify.components.ContactItem
import com.example.contactify.data.Contact
import com.example.contactify.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    viewModel: ContactViewModel,
    onContactClick: (Contact) -> Unit,
    onEditContact: (Contact) -> Unit,
    onSearchClick: () -> Unit,
    onGroupClick: () -> Unit,
    onStatisticsClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Trạng thái menu trên TopAppBar
    var showTopMenu by remember {
        mutableStateOf(false)
    }

    // Contact đang được nhấn giữ
    var selectedContact by remember {
        mutableStateOf<Contact?>(null)
    }

    // Trạng thái Context Menu
    var showContextMenu by remember {
        mutableStateOf(false)
    }

    // Danh sách Contact
    val contacts = viewModel.getFilteredContacts()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Danh bạ")
                },

                actions = {
                    Box {

                        // Nút ⋮
                        IconButton(
                            onClick = {
                                showTopMenu = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }

                        // Menu chính
                        ContactDropdownMenu(
                            expanded = showTopMenu,

                            onDismiss = {
                                showTopMenu = false
                            },

                            onSearch = {
                                onSearchClick()
                            },

                            onGroup = {
                                onGroupClick()
                            },

                            onStatistics = {
                                onStatisticsClick()
                            },

                            onTheme = {
                                Toast.makeText(
                                    context,
                                    "Chức năng giao diện",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Danh sách Contact
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                items(
                    items = contacts,
                    key = { it.id }
                ) { contact ->

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        // Item Contact
                        ContactItem(
                            contact = contact,

                            // Click bình thường
                            onClick = {
                                viewModel.selectContact(contact)
                                onContactClick(contact)
                            },

                            // Nhấn giữ
                            onLongClick = {
                                selectedContact = contact
                                showContextMenu = true
                            }
                        )

                        // Context Menu khi nhấn giữ
                        if (
                            showContextMenu &&
                            selectedContact?.id == contact.id
                        ) {
                            ContactContextMenu(
                                expanded = true,
                                contact = contact,

                                // Đóng menu
                                onDismiss = {
                                    showContextMenu = false
                                    selectedContact = null
                                },

                                // 📞 Gọi
                                onCall = {
                                    val intent = Intent(
                                        Intent.ACTION_DIAL,
                                        Uri.parse("tel:${contact.phone}")
                                    )

                                    context.startActivity(intent)
                                },

                                // ✏️ Sửa
                                onEdit = {
                                    onEditContact(contact)
                                },

                                // ⭐ Yêu thích
                                onFavorite = {
                                    viewModel.toggleFavorite(contact)
                                },

                                // 📋 Sao chép
                                onCopy = {
                                    clipboardManager.setText(
                                        AnnotatedString(contact.phone)
                                    )

                                    Toast.makeText(
                                        context,
                                        "Đã sao chép số điện thoại",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },

                                // 🗑️ Xóa
                                onDelete = {
                                    viewModel.deleteContact(contact)

                                    Toast.makeText(
                                        context,
                                        "Đã xóa liên hệ",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}