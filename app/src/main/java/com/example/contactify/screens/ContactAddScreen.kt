package com.example.contactify.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.contactify.data.PhoneNumber
import com.example.contactify.viewmodel.ContactViewModel

// Danh sách nhóm gợi ý
private val SUGGESTED_GROUPS = listOf("Bạn bè", "Gia đình", "Trường học", "Công việc", "Khác")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactAddScreen(
    viewModel: ContactViewModel,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    // ── Trạng thái form ──────────────────────────────────
    var name  by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("Khác") }
    var showGroupMenu by remember { mutableStateOf(false) }

    // Danh sách số điện thoại (ít nhất 1)
    var phones by remember {
        mutableStateOf(listOf(PhoneNumber(contactId = 0, number = "", label = "Cá nhân")))
    }

    // ── Lỗi validation ───────────────────────────────────
    var nameError  by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        nameError = if (name.isBlank()) "Tên không được để trống" else null
        phoneError = if (phones.first().number.isBlank()) "Số điện thoại chính không được để trống" else null
        return nameError == null && phoneError == null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm liên hệ mới") },
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

            // ── Tên ─────────────────────────────────────
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = null },
                    label = { Text("Tên liên hệ *") },
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // ── Danh sách số điện thoại ──────────────────
            itemsIndexed(phones) { index, phone ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = phone.number,
                        onValueChange = { newNum ->
                            phones = phones.toMutableList().also { it[index] = phone.copy(number = newNum) }
                            if (index == 0) phoneError = null
                        },
                        label = { Text(if (index == 0) "Số điện thoại * (${phone.label})" else "Số phụ (${phone.label})") },
                        isError = index == 0 && phoneError != null,
                        supportingText = { if (index == 0) phoneError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    // Xóa số phụ (chỉ cho số từ thứ 2 trở đi)
                    if (index > 0) {
                        IconButton(onClick = {
                            phones = phones.toMutableList().also { it.removeAt(index) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Xóa số")
                        }
                    }
                }
            }

            // ── Nút thêm số điện thoại ───────────────────
            item {
                val labels = listOf("Cá nhân", "Công việc", "Nhà", "Khác")
                val usedLabels = phones.map { it.label }
                val nextLabel = labels.firstOrNull { it !in usedLabels } ?: "Khác"
                TextButton(
                    onClick = {
                        phones = phones + PhoneNumber(contactId = 0, number = "", label = nextLabel)
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Thêm số điện thoại")
                }
            }

            // ── Email ────────────────────────────────────
            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // ── Nhóm (dropdown) ──────────────────────────
            item {
                ExposedDropdownMenuBox(
                    expanded = showGroupMenu,
                    onExpandedChange = { showGroupMenu = it }
                ) {
                    OutlinedTextField(
                        value = group,
                        onValueChange = { group = it },
                        label = { Text("Nhóm") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGroupMenu) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = showGroupMenu,
                        onDismissRequest = { showGroupMenu = false }
                    ) {
                        SUGGESTED_GROUPS.forEach { suggestion ->
                            DropdownMenuItem(
                                text = { Text(suggestion) },
                                onClick = { group = suggestion; showGroupMenu = false }
                            )
                        }
                    }
                }
            }

            // ── Nút Lưu / Hủy ───────────────────────────
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (validate()) {
                            val mainPhone = phones.first().number
                            val extraPhones = phones.drop(1)
                            viewModel.addContact(
                                name = name.trim(),
                                phone = mainPhone.trim(),
                                email = email.trim(),
                                group = group.trim(),
                                extraPhones = extraPhones
                            )
                            onSaved()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lưu liên hệ")
                }
            }

            item {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Hủy")
                }
            }
        }
    }
}
