package com.mice.smooth.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.Path

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
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<List<Book>>>
}
