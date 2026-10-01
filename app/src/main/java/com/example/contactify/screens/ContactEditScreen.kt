package com.example.contactify.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contactify.data.Contact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactEditScreen(
    contact: Contact,
    onSave: (
        name: String,
        phone: String,
        email: String,
        group: String
    ) -> Unit,
    onCancel: () -> Unit
) {

    // Trạng thái các ô nhập liệu
    var name by remember {
        mutableStateOf(contact.name)
    }

    var phone by remember {
        mutableStateOf(contact.phone)
    }

    var email by remember {
        mutableStateOf(contact.email)
    }

    var group by remember {
        mutableStateOf(contact.group)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Sửa liên hệ")
                },

                navigationIcon = {
                    IconButton(
                        onClick = onCancel
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
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {

            // =========================
            // TÊN
            // =========================
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = {
                    Text("Tên liên hệ")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // =========================
            // SỐ ĐIỆN THOẠI
            // =========================
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                },
                label = {
                    Text("Số điện thoại")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // =========================
            // EMAIL
            // =========================
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = {
                    Text("Email")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // =========================
            // NHÓM
            // =========================
            OutlinedTextField(
                value = group,
                onValueChange = {
                    group = it
                },
                label = {
                    Text("Nhóm")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // =========================
            // NÚT LƯU
            // =========================
            Button(
                onClick = {
                    onSave(
                        name,
                        phone,
                        email,
                        group
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lưu thay đổi")
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =========================
            // NÚT HỦY
            // =========================
            Button(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hủy")
            }
        }
    }
}