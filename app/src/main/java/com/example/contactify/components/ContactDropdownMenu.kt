package com.example.contactify.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ContactDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onSearch: () -> Unit,
    onGroup: () -> Unit,
    onStatistics: () -> Unit,
    onTheme: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {

        // 🔍 Tìm kiếm
        DropdownMenuItem(
            text = {
                Text("🔍 Tìm kiếm")
            },
            onClick = {
                onDismiss()
                onSearch()
            }
        )

        // 👥 Nhóm
        DropdownMenuItem(
            text = {
                Text("👥 Nhóm")
            },
            onClick = {
                onDismiss()
                onGroup()
            }
        )

        // 📊 Thống kê
        DropdownMenuItem(
            text = {
                Text("📊 Thống kê")
            },
            onClick = {
                onDismiss()
                onStatistics()
            }
        )

        // 🌓 Giao diện
        DropdownMenuItem(
            text = {
                Text("🌓 Giao diện")
            },
            onClick = {
                onDismiss()
                onTheme()
            }
        )
    }
}