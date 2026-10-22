package com.example.contactify.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity cho bảng phone_numbers.
 * Quan hệ N-1 với Contact: mỗi liên hệ có thể có nhiều số điện thoại.
 * Khi xóa Contact → tất cả số điện thoại liên quan cũng bị xóa (CASCADE).
 */
@Entity(
    tableName = "phone_numbers",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["contactId"])]
)
data class PhoneNumber(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val contactId: Int,          // Khóa ngoại → Contact.id
    val number: String,          // Số điện thoại
    val label: String = "Cá nhân" // Nhãn: Cá nhân / Công việc / Nhà...
)
