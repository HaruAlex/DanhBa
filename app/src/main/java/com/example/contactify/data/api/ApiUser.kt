package com.example.contactify.data.api

import com.google.gson.annotations.SerializedName

// ─── Response wrapper từ JSONPlaceholder /users ───────────────────────────────

data class ApiUser(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
    val address: ApiAddress,
    val company: ApiCompany
)

data class ApiAddress(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val geo: ApiGeo
)

data class ApiGeo(
    val lat: String,
    val lng: String
)

data class ApiCompany(
    val name: String,
    @SerializedName("catchPhrase") val catchPhrase: String,
    val bs: String
)

// ─── Request body khi tạo / cập nhật user ─────────────────────────────────────

data class ApiUserRequest(
    val name: String,
    val username: String,
    val email: String,
    val phone: String = "",
    val website: String = ""
)
