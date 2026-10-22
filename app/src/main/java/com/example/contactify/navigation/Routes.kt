package com.example.contactify.navigation

/**
 * File duy nhất khai báo tất cả tên route trong ứng dụng.
 * Tách biệt khỏi AppNavigation để dễ tham chiếu từ bất kỳ nơi nào
 * mà không cần import cả NavController.
 */
object Routes {

    // ── Xác thực ────────────────────────────────────────
    const val LOGIN     = "login"
    const val REGISTER  = "register"

    // ── Danh bạ chính ───────────────────────────────────
    const val CONTACTS  = "contacts"
    const val ADD       = "add"
    const val DETAIL    = "detail/{contactId}"
    const val EDIT      = "edit/{contactId}"

    // ── Màn hình phụ ────────────────────────────────────
    const val SEARCH    = "search"
    const val GROUP     = "group"
    const val FAVORITES = "favorites"
    const val STATISTICS = "statistics"

    // ── API Users ────────────────────────────────────────
    const val API_USERS  = "api_users"
    const val API_USER_DETAIL = "api_user_detail/{userId}"
    const val API_USER_ADD    = "api_user_add"
    const val API_USER_EDIT   = "api_user_edit/{userId}"

    // ── Helper: tạo route động ────────────────────────────
    fun detail(contactId: Int) = "detail/$contactId"
    fun edit(contactId: Int)   = "edit/$contactId"
    fun apiUserDetail(userId: Int) = "api_user_detail/$userId"
    fun apiUserEdit(userId: Int)   = "api_user_edit/$userId"
}
