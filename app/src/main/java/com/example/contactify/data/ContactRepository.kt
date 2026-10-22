package com.example.contactify.data

import androidx.lifecycle.LiveData

/**
 * Repository: lớp trung gian giữa ViewModel và Room DAO.
 * Ẩn chi tiết nguồn dữ liệu (Room DB / System Contacts) khỏi ViewModel.
 */
class ContactRepository(private val dao: ContactDao) {

    // ════════════════════════════════════════
    // QUAN SÁT DỮ LIỆU (LiveData — tự động cập nhật UI)
    // ════════════════════════════════════════

    val allContacts: LiveData<List<Contact>> = dao.getAllContacts()
    val allContactsWithPhones: LiveData<List<ContactWithPhones>> = dao.getAllContactsWithPhones()
    val favoriteContacts: LiveData<List<Contact>> = dao.getFavoriteContacts()
    val totalCount: LiveData<Int> = dao.getTotalCount()
    val favoriteCount: LiveData<Int> = dao.getFavoriteCount()
    val allGroups: LiveData<List<String>> = dao.getAllGroups()

    fun searchContacts(query: String): LiveData<List<Contact>> =
        dao.searchContacts(query)

    fun getContactsByGroup(group: String): LiveData<List<Contact>> =
        dao.getContactsByGroup(group)

    fun getGroupCount(group: String): LiveData<Int> =
        dao.getGroupCount(group)

    // ════════════════════════════════════════
    // THAO TÁC GHI (suspend — chạy trong coroutine)
    // ════════════════════════════════════════

    suspend fun getContactById(id: Int): Contact? = dao.getContactById(id)

    suspend fun getContactWithPhonesById(id: Int): ContactWithPhones? =
        dao.getContactWithPhonesById(id)

    /**
     * Thêm liên hệ mới kèm danh sách số điện thoại.
     * @return id của contact vừa tạo
     */
    suspend fun insertContact(contact: Contact, phones: List<PhoneNumber>): Long {
        val newId = dao.insertContact(contact)
        val phonesWithId = phones.map { it.copy(contactId = newId.toInt()) }
        dao.insertPhoneNumbers(phonesWithId)
        return newId
    }

    /**
     * Cập nhật contact và thay thế toàn bộ danh sách số điện thoại.
     */
    suspend fun updateContact(contact: Contact, phones: List<PhoneNumber>) {
        dao.updateContact(contact)
        // Xóa số cũ, chèn số mới
        dao.deletePhonesByContactId(contact.id)
        val phonesWithId = phones.map { it.copy(contactId = contact.id) }
        dao.insertPhoneNumbers(phonesWithId)
    }

    suspend fun deleteContact(contact: Contact) = dao.deleteContact(contact)

    suspend fun deleteContactsByIds(ids: List<Int>) = dao.deleteContactsByIds(ids)

    suspend fun deleteAllContacts() = dao.deleteAllContacts()

    /**
     * Toggle yêu thích — lấy contact hiện tại, đảo isFavorite rồi update.
     */
    suspend fun toggleFavorite(contact: Contact) {
        dao.updateContact(contact.copy(isFavorite = !contact.isFavorite))
    }
}
