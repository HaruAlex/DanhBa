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

private val SUGGESTED_GROUPS_EDIT = listOf("Bạn bè", "Gia đình", "Trường học", "Công việc", "Khác")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactEditScreen(
    contactId: Int,
    viewModel: ContactViewModel,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    var loaded by remember { mutableStateOf(false) }
    var name   by remember { mutableStateOf("") }
    var email  by remember { mutableStateOf("") }
    var group  by remember { mutableStateOf("Khác") }
    var phones by remember { mutableStateOf(listOf(PhoneNumber(contactId = contactId, number = "", label = "Cá nhân"))) }
    var showGroupMenu by remember { mutableStateOf(false) }

    var nameError  by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    // Tải dữ liệu liên hệ lần đầu
    LaunchedEffect(contactId) {
        val cwp = viewModel.getContactWithPhonesById(contactId)
        if (cwp != null) {
            name  = cwp.contact.name
            email = cwp.contact.email
            group = cwp.contact.group
            phones = cwp.phones.ifEmpty {
                listOf(PhoneNumber(contactId = contactId, number = cwp.contact.phone, label = "Cá nhân"))
            }
        }
        loaded = true
    }

    if (!loaded) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    fun validate(): Boolean {
        nameError  = if (name.isBlank()) "Tên không được để trống" else null
        phoneError = if (phones.firstOrNull()?.number.isNullOrBlank()) "Số điện thoại chính không được để trống" else null
        return nameError == null && phoneError == null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sửa liên hệ") },
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

            // ── Số điện thoại ────────────────────────────
            itemsIndexed(phones) { index, phone ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = phone.number,
                        onValueChange = { num ->
                            phones = phones.toMutableList().also { it[index] = phone.copy(number = num) }
                            if (index == 0) phoneError = null
                        },
                        label = { Text(if (index == 0) "Số chính * (${phone.label})" else "Số phụ (${phone.label})") },
                        isError = index == 0 && phoneError != null,
                        supportingText = { if (index == 0) phoneError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    if (index > 0) {
                        IconButton(onClick = {
                            phones = phones.toMutableList().also { it.removeAt(index) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Xóa số")
                        }
                    }
                }
            }

            // ── Thêm số điện thoại ────────────────────────
            item {
                TextButton(onClick = {
                    val labels    = listOf("Cá nhân", "Công việc", "Nhà", "Khác")
                    val usedLabels = phones.map { it.label }
                    val nextLabel  = labels.firstOrNull { it !in usedLabels } ?: "Khác"
                    phones = phones + PhoneNumber(contactId = contactId, number = "", label = nextLabel)
                }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Thêm số điện thoại")
                }
            }

            // ── Email ─────────────────────────────────────
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
                        SUGGESTED_GROUPS_EDIT.forEach { suggestion ->
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
                            viewModel.updateContact(
                                id     = contactId,
                                name   = name.trim(),
                                phone  = phones.firstOrNull()?.number?.trim() ?: "",
                                email  = email.trim(),
                                group  = group.trim(),
                                phones = phones
                            )
                            onSaved()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Lưu thay đổi") }
            }

            item {
                OutlinedButton(
                    onClick  = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Hủy") }
            }
        }
    }
}
