package com.example.contactify.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class kết hợp Contact với danh sách PhoneNumber của nó.
 * Room tự động truy vấn và lắp ghép khi dùng @Transaction + @Query.
 */
data class ContactWithPhones(
    @Embedded val contact: Contact,
    @Relation(
        parentColumn = "id",
        entityColumn = "contactId"
    )
    val phones: List<PhoneNumber>
)
