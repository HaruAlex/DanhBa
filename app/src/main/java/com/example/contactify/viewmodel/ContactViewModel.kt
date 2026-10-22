package com.example.contactify.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.contactify.data.Contact
import com.example.contactify.data.ContactDatabase
import com.example.contactify.data.ContactRepository
import com.example.contactify.data.ContactWithPhones
import com.example.contactify.data.PhoneNumber
import kotlinx.coroutines.launch

/**
 * ViewModel trung tâm của ứng dụng.
 * - Dùng AndroidViewModel để lấy Application context khởi tạo Room.
 * - LiveData đảm bảo UI tự cập nhật khi dữ liệu thay đổi.
 * - Coroutines xử lý mọi thao tác DB bất đồng bộ.
 */
class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ContactRepository

    // ════════════════════════════════════════
    // TRẠNG THÁI TÌM KIẾM
    // ════════════════════════════════════════

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    // ════════════════════════════════════════
    // LIVE DATA TỪ ROOM
    // ════════════════════════════════════════

    /** Toàn bộ danh sách liên hệ (có lọc khi searchQuery != "") */
    val contacts: LiveData<List<Contact>>

    val contactsWithPhones: LiveData<List<ContactWithPhones>>
    val favoriteContacts: LiveData<List<Contact>>
    val allGroups: LiveData<List<String>>
    val totalCount: LiveData<Int>
    val favoriteCount: LiveData<Int>

    // ════════════════════════════════════════
    // TRẠNG THÁI UI (Compose State)
    // ════════════════════════════════════════

    /** Tập hợp id các liên hệ đang được chọn (multi-select) */
    var selectedIds by mutableStateOf<Set<Int>>(emptySet())
        private set

    /** true = đang ở chế độ chọn nhiều */
    var isMultiSelectMode by mutableStateOf(false)
        private set

    /** Dark mode toggle */
    var isDarkMode by mutableStateOf(false)

    init {
        val dao = ContactDatabase.getDatabase(application).contactDao()
        repository = ContactRepository(dao)

        // contacts tự lọc theo searchQuery qua switchMap
        contacts = _searchQuery.switchMap { query ->
            if (query.isBlank()) repository.allContacts
            else repository.searchContacts(query)
        }

        contactsWithPhones = repository.allContactsWithPhones
        favoriteContacts = repository.favoriteContacts
        allGroups = repository.allGroups
        totalCount = repository.totalCount
        favoriteCount = repository.favoriteCount
    }

    // ════════════════════════════════════════
    // TÌM KIẾM
    // ════════════════════════════════════════

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }

    // ════════════════════════════════════════
    // THÊM LIÊN HỆ
    // ════════════════════════════════════════

    fun addContact(
        name: String,
        phone: String,
        email: String,
        group: String,
        extraPhones: List<PhoneNumber> = emptyList()
    ) = viewModelScope.launch {
        val contact = Contact(
            name = name,
            phone = phone,
            email = email,
            group = group
        )
        // Gộp số chính + số phụ
        val allPhones = mutableListOf(
            PhoneNumber(contactId = 0, number = phone, label = "Cá nhân")
        )
        allPhones.addAll(extraPhones)
        repository.insertContact(contact, allPhones)
    }

    // ════════════════════════════════════════
    // CẬP NHẬT LIÊN HỆ
    // ════════════════════════════════════════

    fun updateContact(
        id: Int,
        name: String,
        phone: String,
        email: String,
        group: String,
        phones: List<PhoneNumber> = emptyList()
    ) = viewModelScope.launch {
        val contact = Contact(
            id = id,
            name = name,
            phone = phone,
            email = email,
            group = group
        )
        val allPhones = if (phones.isEmpty()) {
            listOf(PhoneNumber(contactId = id, number = phone, label = "Cá nhân"))
        } else phones
        repository.updateContact(contact, allPhones)
    }

    // ════════════════════════════════════════
    // XÓA LIÊN HỆ
    // ════════════════════════════════════════

    fun deleteContact(contact: Contact) = viewModelScope.launch {
        repository.deleteContact(contact)
        // Bỏ chọn nếu đang được chọn
        selectedIds = selectedIds - contact.id
    }

    fun deleteAllContacts() = viewModelScope.launch {
        repository.deleteAllContacts()
        clearMultiSelect()
    }

    // ════════════════════════════════════════
    // XÓA NHIỀU LIÊN HỆ (MULTI-SELECT)
    // ════════════════════════════════════════

    fun deleteSelectedContacts() = viewModelScope.launch {
        if (selectedIds.isNotEmpty()) {
            repository.deleteContactsByIds(selectedIds.toList())
            clearMultiSelect()
        }
    }

    // ════════════════════════════════════════
    // YÊU THÍCH
    // ════════════════════════════════════════

    fun toggleFavorite(contact: Contact) = viewModelScope.launch {
        repository.toggleFavorite(contact)
    }

    // ════════════════════════════════════════
    // LỌC THEO NHÓM
    // ════════════════════════════════════════

    fun getContactsByGroup(group: String): LiveData<List<Contact>> =
        repository.getContactsByGroup(group)

    fun getGroupCount(group: String): LiveData<Int> =
        repository.getGroupCount(group)

    // ════════════════════════════════════════
    // MULTI-SELECT
    // ════════════════════════════════════════

    fun toggleSelection(contactId: Int) {
        selectedIds = if (selectedIds.contains(contactId)) {
            selectedIds - contactId
        } else {
            selectedIds + contactId
        }
        isMultiSelectMode = selectedIds.isNotEmpty()
    }

    fun selectAll(contactList: List<Contact>) {
        selectedIds = contactList.map { it.id }.toSet()
        isMultiSelectMode = true
    }

    fun clearMultiSelect() {
        selectedIds = emptySet()
        isMultiSelectMode = false
    }

    // ════════════════════════════════════════
    // LẤY CONTACT THEO ID (suspend)
    // ════════════════════════════════════════

    suspend fun getContactById(id: Int): Contact? =
        repository.getContactById(id)

    suspend fun getContactWithPhonesById(id: Int): ContactWithPhones? =
        repository.getContactWithPhonesById(id)
}
