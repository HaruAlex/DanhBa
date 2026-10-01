package com.example.contactify.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.contactify.data.Contact
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ContactViewModel : ViewModel() {

    // Danh sách liên hệ
    private val _contacts = mutableStateListOf(
        Contact(
            id = 1,
            name = "Nguyễn Văn A",
            phone = "0123456789",
            email = "vana@gmail.com",
            group = "Bạn bè",
            isFavorite = true
        ),
        Contact(
            id = 2,
            name = "Trần Thị B",
            phone = "0987654321",
            email = "thib@gmail.com",
            group = "Gia đình"
        ),
        Contact(
            id = 3,
            name = "Lê Văn C",
            phone = "0912345678",
            email = "vanc@gmail.com",
            group = "Trường học"
        )
    )

    // Cho UI đọc danh sách
    val contacts: List<Contact>
        get() = _contacts

    // Contact đang được chọn
    var selectedContact: Contact? = null
        private set

    // Trạng thái tìm kiếm
    private var _searchQuery by mutableStateOf("")

    val searchQuery: String
        get() = _searchQuery


    // =========================
    // Thêm liên hệ
    // =========================
    fun addContact(
        name: String,
        phone: String,
        email: String,
        group: String
    ) {
        val newId = if (_contacts.isEmpty()) {
            1
        } else {
            _contacts.maxOf { it.id } + 1
        }

        _contacts.add(
            Contact(
                id = newId,
                name = name,
                phone = phone,
                email = email,
                group = group
            )
        )
    }

    // =========================
    // Chọn Contact
    // =========================
    fun selectContact(contact: Contact) {
        selectedContact = contact
    }

    // =========================
    // Xóa Contact
    // =========================
    fun deleteContact(contact: Contact) {
        _contacts.remove(contact)

        if (selectedContact?.id == contact.id) {
            selectedContact = null
        }
    }

    // =========================
    // Xóa tất cả
    // =========================
    fun deleteAllContacts() {
        _contacts.clear()
        selectedContact = null
    }

    // =========================
    // Cập nhật Contact
    // =========================
    fun updateContact(
        id: Int,
        name: String,
        phone: String,
        email: String,
        group: String
    ) {
        val index = _contacts.indexOfFirst { it.id == id }

        if (index != -1) {
            val oldContact = _contacts[index]

            _contacts[index] = oldContact.copy(
                name = name,
                phone = phone,
                email = email,
                group = group
            )

            selectedContact = _contacts[index]
        }
    }

    // =========================
    // Yêu thích
    // =========================
    fun toggleFavorite(contact: Contact) {
        val index = _contacts.indexOfFirst {
            it.id == contact.id
        }

        if (index != -1) {
            _contacts[index] = _contacts[index].copy(
                isFavorite = !_contacts[index].isFavorite
            )

            if (selectedContact?.id == contact.id) {
                selectedContact = _contacts[index]
            }
        }
    }

    // =========================
    // Tìm kiếm
    // =========================
    fun setSearchQuery(query: String) {
        _searchQuery = query
    }

    fun getFilteredContacts(): List<Contact> {
        if (searchQuery.isBlank()) {
            return contacts
        }

        return contacts.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.phone.contains(searchQuery)
        }
    }

    // =========================
    // Lọc yêu thích
    // =========================
    fun getFavoriteContacts(): List<Contact> {
        return contacts.filter {
            it.isFavorite
        }
    }

    // =========================
    // Lọc theo nhóm
    // =========================
    fun getContactsByGroup(group: String): List<Contact> {
        return contacts.filter {
            it.group == group
        }
    }

    // =========================
    // Thống kê
    // =========================
    fun getTotalContacts(): Int {
        return contacts.size
    }

    fun getFavoriteCount(): Int {
        return contacts.count {
            it.isFavorite
        }
    }

    fun getGroupCount(group: String): Int {
        return contacts.count {
            it.group == group
        }
    }
}