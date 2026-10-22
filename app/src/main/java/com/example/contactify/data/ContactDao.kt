package com.example.contactify.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ContactDao {

    // ════════════════════════════════════════
    // CONTACT CRUD
    // ════════════════════════════════════════

    /** Trả về toàn bộ danh sách, tự động cập nhật UI qua LiveData */
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): LiveData<List<Contact>>

    /** Tìm kiếm theo tên hoặc số điện thoại chính */
    @Query("""
        SELECT * FROM contacts 
        WHERE name LIKE '%' || :query || '%' 
           OR phone LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchContacts(query: String): LiveData<List<Contact>>

    /** Lọc theo nhóm */
    @Query("SELECT * FROM contacts WHERE `group` = :group ORDER BY name ASC")
    fun getContactsByGroup(group: String): LiveData<List<Contact>>

    /** Chỉ lấy liên hệ yêu thích */
    @Query("SELECT * FROM contacts WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteContacts(): LiveData<List<Contact>>

    /** Lấy một Contact theo id (suspend — dùng trong coroutine) */
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Int): Contact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact): Long

    @Update
    suspend fun updateContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    /** Xóa nhiều liên hệ cùng lúc theo danh sách id */
    @Query("DELETE FROM contacts WHERE id IN (:ids)")
    suspend fun deleteContactsByIds(ids: List<Int>)

    /** Xóa tất cả */
    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()

    // ════════════════════════════════════════
    // PHONE NUMBER CRUD
    // ════════════════════════════════════════

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoneNumbers(phones: List<PhoneNumber>)

    @Delete
    suspend fun deletePhoneNumber(phone: PhoneNumber)

    /** Xóa tất cả số điện thoại của một liên hệ (dùng khi cập nhật lại) */
    @Query("DELETE FROM phone_numbers WHERE contactId = :contactId")
    suspend fun deletePhonesByContactId(contactId: Int)

    @Query("SELECT * FROM phone_numbers WHERE contactId = :contactId")
    suspend fun getPhonesByContactId(contactId: Int): List<PhoneNumber>

    // ════════════════════════════════════════
    // CONTACT WITH PHONES (Relation)
    // ════════════════════════════════════════

    @Transaction
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContactsWithPhones(): LiveData<List<ContactWithPhones>>

    @Transaction
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactWithPhonesById(id: Int): ContactWithPhones?

    // ════════════════════════════════════════
    // THỐNG KÊ
    // ════════════════════════════════════════

    @Query("SELECT COUNT(*) FROM contacts")
    fun getTotalCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM contacts WHERE isFavorite = 1")
    fun getFavoriteCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM contacts WHERE `group` = :group")
    fun getGroupCount(group: String): LiveData<Int>

    @Query("SELECT DISTINCT `group` FROM contacts WHERE `group` != '' ORDER BY `group` ASC")
    fun getAllGroups(): LiveData<List<String>>
}
