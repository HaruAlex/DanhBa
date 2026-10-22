package com.example.contactify.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Menu thả xuống (⋮) trên ContactScreen.
 * Chứa các tùy chọn: Tìm kiếm, Nhóm, Yêu thích, Thống kê, API Users, Dark mode, Thoát.
 */
@Composable
fun ContactDropdownMenu(
    expanded: Boolean,
    isDarkMode: Boolean,
    onDismiss: () -> Unit,
    onSearch: () -> Unit,
    onGroup: () -> Unit,
    onFavorites: () -> Unit,
    onStatistics: () -> Unit,
    onApiUsers: () -> Unit,
    onTheme: () -> Unit,
    onExit: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {

        // 🔍 Tìm kiếm
        DropdownMenuItem(
            text = { Text("Tìm kiếm") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            onClick = {
                onDismiss()
                onSearch()
            }
        )

        // 👥 Nhóm
        DropdownMenuItem(
            text = { Text("Nhóm liên hệ") },
            leadingIcon = {
                Icon(Icons.Default.Group, contentDescription = null)
            },
            onClick = {
                onDismiss()
                onGroup()
            }
        )

        // ⭐ Yêu thích
        DropdownMenuItem(
            text = { Text("Yêu thích") },
            leadingIcon = {
                Icon(Icons.Default.Star, contentDescription = null)
            },
            onClick = {
                onDismiss()
                onFavorites()
            }
        )

        // 📊 Thống kê
        DropdownMenuItem(
            text = { Text("Thống kê") },
            leadingIcon = {
                Icon(Icons.Default.BarChart, contentDescription = null)
            },
            onClick = {
                onDismiss()
                onStatistics()
            }
        )

        // 🌐 Quản lý API Users
        DropdownMenuItem(
            text = { Text("Quản lý người dùng (API)") },
            leadingIcon = {
                Icon(Icons.Default.CloudQueue, contentDescription = null)
            },
            onClick = {
                onDismiss()
                onApiUsers()
            }
        )

        // 🌓 Giao diện sáng/tối
        DropdownMenuItem(
            text = {
                Text(if (isDarkMode) "Chế độ sáng" else "Chế độ tối")
            },
            leadingIcon = {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = null
                )
            },
            onClick = {
                onDismiss()
                onTheme()
            }
        )

        // 🚪 Thoát ứng dụng
        DropdownMenuItem(
            text = { Text("Thoát ứng dụng") },
            leadingIcon = {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
            },
            onClick = {
                onDismiss()
                onExit()
            }
        )
    }
}
