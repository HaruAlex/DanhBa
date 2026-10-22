package com.example.contactify.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity cho bảng contacts.
 * Mỗi liên hệ có thể có nhiều số điện thoại — lưu trong bảng PhoneNumber riêng
 * liên kết qua contactId (quan hệ 1-N).
 */
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String = "",
    val group: String = "Khác",
    val isFavorite: Boolean = false,
    // Số điện thoại chính — tiện dùng nhanh mà không cần join
    val phone: String = ""
)
