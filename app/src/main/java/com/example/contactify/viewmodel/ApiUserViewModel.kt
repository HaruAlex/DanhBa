package com.example.contactify.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactify.data.api.ApiUser
import com.example.contactify.data.api.ApiUserRequest
import com.example.contactify.data.api.RetrofitInstance
import kotlinx.coroutines.launch

/**
 * Trạng thái màn hình API Users.
 */
sealed class ApiUiState {
    object Idle : ApiUiState()
    object Loading : ApiUiState()
    data class Success(val message: String = "") : ApiUiState()
    data class Error(val message: String) : ApiUiState()
}

class ApiUserViewModel : ViewModel() {

    // ════════════════════════════════════════
    // TRẠNG THÁI
    // ════════════════════════════════════════

    var users by mutableStateOf<List<ApiUser>>(emptyList())
        private set

    var selectedUser by mutableStateOf<ApiUser?>(null)
        private set

    var uiState by mutableStateOf<ApiUiState>(ApiUiState.Idle)
        private set

    // ════════════════════════════════════════
    // LẤY DANH SÁCH USERS
    // ════════════════════════════════════════

    fun loadUsers() {
        viewModelScope.launch {
            uiState = ApiUiState.Loading
            try {
                val response = RetrofitInstance.api.getUsers()
                if (response.isSuccessful) {
                    users = response.body() ?: emptyList()
                    uiState = ApiUiState.Success()
                } else {
                    uiState = ApiUiState.Error("Lỗi ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                uiState = ApiUiState.Error("Không thể tải dữ liệu: ${e.localizedMessage}")
            }
        }
    }

    // ════════════════════════════════════════
    // LẤY THÔNG TIN CHI TIẾT USER THEO ID
    // ════════════════════════════════════════

    fun loadUserById(id: Int) {
        viewModelScope.launch {
            uiState = ApiUiState.Loading
            try {
                val response = RetrofitInstance.api.getUserById(id)
                if (response.isSuccessful) {
                    selectedUser = response.body()
                    uiState = ApiUiState.Success()
                } else {
                    uiState = ApiUiState.Error("Không tìm thấy user ID=$id")
                }
            } catch (e: Exception) {
                uiState = ApiUiState.Error("Lỗi kết nối: ${e.localizedMessage}")
            }
        }
    }

    // ════════════════════════════════════════
    // THÊM USER MỚI
    // ════════════════════════════════════════

    fun createUser(request: ApiUserRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = ApiUiState.Loading
            try {
                val response = RetrofitInstance.api.createUser(request)
                if (response.isSuccessful) {
                    val newUser = response.body()
                    if (newUser != null) {
                        // JSONPlaceholder trả về user với id=11 (fake) — thêm vào đầu list
                        users = listOf(newUser) + users
                    }
                    uiState = ApiUiState.Success("Đã tạo user thành công")
                    onSuccess()
                } else {
                    uiState = ApiUiState.Error("Tạo user thất bại: ${response.code()}")
                }
            } catch (e: Exception) {
                uiState = ApiUiState.Error("Lỗi kết nối: ${e.localizedMessage}")
            }
        }
    }

    // ════════════════════════════════════════
    // CẬP NHẬT USER
    // ════════════════════════════════════════

    fun updateUser(id: Int, request: ApiUserRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = ApiUiState.Loading
            try {
                val response = RetrofitInstance.api.updateUser(id, request)
                if (response.isSuccessful) {
                    val updatedUser = response.body()
                    if (updatedUser != null) {
                        users = users.map { if (it.id == id) updatedUser else it }
                        if (selectedUser?.id == id) selectedUser = updatedUser
                    }
                    uiState = ApiUiState.Success("Đã cập nhật user")
                    onSuccess()
                } else {
                    uiState = ApiUiState.Error("Cập nhật thất bại: ${response.code()}")
                }
            } catch (e: Exception) {
                uiState = ApiUiState.Error("Lỗi kết nối: ${e.localizedMessage}")
            }
        }
    }

    // ════════════════════════════════════════
    // XÓA USER
    // ════════════════════════════════════════

    fun deleteUser(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = ApiUiState.Loading
            try {
                val response = RetrofitInstance.api.deleteUser(id)
                if (response.isSuccessful) {
                    users = users.filter { it.id != id }
                    if (selectedUser?.id == id) selectedUser = null
                    uiState = ApiUiState.Success("Đã xóa user")
                    onSuccess()
                } else {
                    uiState = ApiUiState.Error("Xóa thất bại: ${response.code()}")
                }
            } catch (e: Exception) {
                uiState = ApiUiState.Error("Lỗi kết nối: ${e.localizedMessage}")
            }
        }
    }

    // ════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════

    fun selectUser(user: ApiUser) { selectedUser = user }

    fun clearState() { uiState = ApiUiState.Idle }

    /** Kiểm tra email hợp lệ */
    fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
