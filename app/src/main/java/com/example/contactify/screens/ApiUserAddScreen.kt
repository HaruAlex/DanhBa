package com.example.contactify.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.contactify.data.api.ApiUserRequest
import com.example.contactify.viewmodel.ApiUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiUserAddScreen(
    viewModel: ApiUserViewModel,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    var name     by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var phone    by remember { mutableStateOf("") }
    var website  by remember { mutableStateOf("") }

    var nameError  by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        nameError  = if (name.isBlank()) "Tên không được để trống" else null
        emailError = when {
            email.isBlank()                -> "Email không được để trống"
            !viewModel.isValidEmail(email) -> "Email không đúng định dạng"
            else                           -> null
        }
        return nameError == null && emailError == null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm người dùng") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = name, onValueChange = { name = it; nameError = null },
                    label = { Text("Họ tên *") },
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = username, onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = email, onValueChange = { email = it; emailError = null },
                    label = { Text("Email *") },
                    isError = emailError != null,
                    supportingText = { emailError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = phone, onValueChange = { phone = it },
                    label = { Text("Điện thoại") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = website, onValueChange = { website = it },
                    label = { Text("Website") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
            }
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (validate()) {
                            viewModel.createUser(
                                ApiUserRequest(
                                    name = name.trim(),
                                    username = username.trim().ifBlank { name.lowercase().replace(" ", "") },
                                    email = email.trim(),
                                    phone = phone.trim(),
                                    website = website.trim()
                                ),
                                onSuccess = onSaved
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Tạo người dùng") }
            }
            item {
                OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
                    Text("Hủy")
                }
            }
        }
    }
}
