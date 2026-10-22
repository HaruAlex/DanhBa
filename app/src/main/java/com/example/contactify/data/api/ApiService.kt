package com.example.contactify.data.api

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit interface cho JSONPlaceholder API.
 * Base URL: https://jsonplaceholder.typicode.com/
 *
 * Tất cả hàm là suspend function → tích hợp Coroutines.
 */
interface ApiService {

    /** Lấy danh sách tất cả người dùng */
    @GET("users")
    suspend fun getUsers(): Response<List<ApiUser>>

    /** Lấy chi tiết người dùng theo ID */
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<ApiUser>

    /** Tạo người dùng mới */
    @POST("users")
    suspend fun createUser(@Body user: ApiUserRequest): Response<ApiUser>

    /** Cập nhật toàn bộ thông tin người dùng */
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body user: ApiUserRequest
    ): Response<ApiUser>

    /** Xóa người dùng */
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>
}
