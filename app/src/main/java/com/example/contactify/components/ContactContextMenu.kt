package com.example.contactify.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.contactify.data.Contact

/**
 * Context Menu xuất hiện khi nhấn giữ vào một liên hệ.
 * Chứa: Gọi điện, Sửa thông tin, Yêu thích, Chuyển nhóm, Sao chép, Xóa.
 */
@Composable
fun ContactContextMenu(
    expanded: Boolean,
    contact: Contact,
    allGroups: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onCall: () -> Unit,
    onEdit: () -> Unit,
    onFavorite: () -> Unit,
    onCopy: () -> Unit,
    onMoveToGroup: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {

        // 📞 Gọi điện
        DropdownMenuItem(
            text = { Text("Gọi điện") },
            leadingIcon = {
                Icon(Icons.Default.Call, contentDescription = null)
            },
            onClick = {
                onCall()
                onDismiss()
            }
        )

        // ✏️ Sửa thông tin
        DropdownMenuItem(
            text = { Text("Sửa thông tin") },
            leadingIcon = {
                Icon(Icons.Default.Edit, contentDescription = null)
            },
            onClick = {
                onEdit()
                onDismiss()
            }
        )

        // ⭐ Yêu thích / Bỏ yêu thích
        DropdownMenuItem(
            text = {
                Text(if (contact.isFavorite) "Bỏ yêu thích" else "Thêm vào yêu thích")
            },
            leadingIcon = {
                Icon(
                    imageVector = if (contact.isFavorite) Icons.Default.StarBorder else Icons.Default.Star,
                    contentDescription = null
                )
            },
            onClick = {
                onFavorite()
                onDismiss()
            }
        )

        // 📂 Chuyển sang nhóm khác
        if (allGroups.isNotEmpty()) {
            DropdownMenuItem(
                text = { Text("Chuyển sang nhóm...") },
                leadingIcon = {
                    Icon(Icons.Default.DriveFileMove, contentDescription = null)
                },
                onClick = {
                    onMoveToGroup()
                    onDismiss()
                }
            )
        }

        // 📋 Sao chép số điện thoại
        DropdownMenuItem(
            text = { Text("Sao chép số điện thoại") },
            leadingIcon = {
                Icon(Icons.Default.ContentCopy, contentDescription = null)
            },
            onClick = {
                onCopy()
                onDismiss()
            }
        )

        // 🗑️ Xóa liên hệ
        DropdownMenuItem(
            text = { Text("Xóa liên hệ") },
            leadingIcon = {
                Icon(Icons.Default.Delete, contentDescription = null)
            },
            onClick = {
                onDelete()
                onDismiss()
            }
        )
    }
}
