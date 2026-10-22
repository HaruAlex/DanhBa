package com.example.contactify.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room Database duy nhất của ứng dụng.
 * Singleton pattern — chỉ tạo một instance trong suốt vòng đời ứng dụng.
 */
@Database(
    entities = [Contact::class, PhoneNumber::class],
    version = 1,
    exportSchema = false
)
abstract class ContactDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: ContactDatabase? = null

        fun getDatabase(context: Context): ContactDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactDatabase::class.java,
                    "contactify_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Chèn dữ liệu mẫu khi database được tạo lần đầu.
     */
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateSampleData(database.contactDao())
                }
            }
        }

        private suspend fun populateSampleData(dao: ContactDao) {
            // Thêm 3 liên hệ mẫu
            val id1 = dao.insertContact(
                Contact(name = "Nguyễn Văn A", email = "vana@gmail.com", group = "Bạn bè", isFavorite = true, phone = "0123456789")
            )
            dao.insertPhoneNumbers(listOf(
                PhoneNumber(contactId = id1.toInt(), number = "0123456789", label = "Cá nhân"),
                PhoneNumber(contactId = id1.toInt(), number = "0987000111", label = "Công việc")
            ))

            val id2 = dao.insertContact(
                Contact(name = "Trần Thị B", email = "thib@gmail.com", group = "Gia đình", phone = "0987654321")
            )
            dao.insertPhoneNumbers(listOf(
                PhoneNumber(contactId = id2.toInt(), number = "0987654321", label = "Cá nhân")
            ))

            val id3 = dao.insertContact(
                Contact(name = "Lê Văn C", email = "vanc@gmail.com", group = "Trường học", phone = "0912345678")
            )
            dao.insertPhoneNumbers(listOf(
                PhoneNumber(contactId = id3.toInt(), number = "0912345678", label = "Cá nhân")
            ))
        }
    }
}
