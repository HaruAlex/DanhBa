package com.example.contactify.data

data class Contact(
    val id: Int,
    var name: String,
    var phone: String,
    var email: String = "",
    var group: String = "Khác",
    var isFavorite: Boolean = false
)