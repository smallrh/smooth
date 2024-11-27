package com.mice.smooth.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)

data class Book(
    val author: String,
    val book_title: String,
    val id: Int,
    val img_path: String,
    val status: String,
    val tags: String
)

data class Chapter(
    val id: Int,
    val title: String,
    val content: String,
    val chapter_page: Int,
)

data class UserBodyRequest(
    val email: String,
    val password: String

)

data class LoginResponse(
    val access_token: String,
    val refresh_token: String
)

interface HttpConnectionApi {
    @GET("books/{id}")
    suspend fun getBookDetail(@Path("id") id: Int): Response<ApiResponse<List<Chapter>>>

    @GET("getTopEightBooks")
    suspend fun getHighRatedBooks(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<List<Book>>>

    @GET("getNewBooks")
    suspend fun getNewBooks(
        @Header("Token") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<List<Book>>>

    @Headers("Content-Type: application/json")
    @POST("/register")
    suspend fun register(@Body request: UserBodyRequest): Response<ApiResponse<Unit>>

    @Headers("Content-Type: application/json")
    @POST("/login")
    suspend fun login(@Body request: UserBodyRequest): Response<LoginResponse>
}
