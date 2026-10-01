package com.example.contactify.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    // Lấy danh sách đã lọc
    val contacts = viewModel.getFilteredContacts()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Tìm kiếm")
                },

                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
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
                .padding(16.dp)
        ) {

            // Ô tìm kiếm
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = {
                    viewModel.setSearchQuery(it)
                },
                label = {
                    Text("Nhập tên hoặc số điện thoại")
                },
                modifier = Modifier
                    .padding(bottom = 12.dp)
            )

            // Kết quả tìm kiếm
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = contacts,
                    key = { it.id }
                ) { contact ->

                    ContactItem(
                        contact = contact,

                        onClick = {
                            onContactClick(contact)
                        },

                        onLongClick = {
                            // Context Menu được xử lý ở ContactScreen
                        }
                    )
                }
            }
        }
    }
}