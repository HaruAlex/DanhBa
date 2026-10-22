package com.example.contactify.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.contactify.data.api.ApiUserRequest
import com.example.contactify.viewmodel.ApiUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiUserEditScreen(
    userId: Int,
    viewModel: ApiUserViewModel,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val user = viewModel.selectedUser

    var name     by remember(user) { mutableStateOf(user?.name ?: "") }
    var username by remember(user) { mutableStateOf(user?.username ?: "") }
    var email    by remember(user) { mutableStateOf(user?.email ?: "") }
    var phone    by remember(user) { mutableStateOf(user?.phone ?: "") }
    var website  by remember(user) { mutableStateOf(user?.website ?: "") }

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

    if (user == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sửa người dùng") },
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
                            viewModel.updateUser(
                                id = userId,
                                request = ApiUserRequest(
                                    name = name.trim(),
                                    username = username.trim(),
                                    email = email.trim(),
                                    phone = phone.trim(),
                                    website = website.trim()
                                ),
                                onSuccess = onSaved
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Lưu thay đổi") }
            }
            item {
                OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
                    Text("Hủy")
                }
            }
        }
    }
}
