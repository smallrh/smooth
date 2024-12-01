
package com.mice.smooth.home.modal

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mice.smooth.api.ApiResponse
import com.mice.smooth.api.Book
import com.mice.smooth.api.RetrofitClient
import com.mice.smooth.home.NewBookItem
import com.mice.smooth.util.RefreshTokenUtil
import com.mice.smooth.util.TagUtil
import com.mice.smooth.util.TokenUtil
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBooksModal(onDismiss: () -> Unit, navController: NavController) {
    val books = remember { mutableStateOf<List<Book>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val tokenUtil = TokenUtil()
    val accessToken = tokenUtil.getAccessTokenFromPreferences(context)
    val refreshToken = tokenUtil.getRefreshTokenFromPreferences(context)?: ""
    var showLoadingDialog by remember { mutableStateOf(false) } // 控制加载对话框的状态
    var showTokenExpiredDialog by remember { mutableStateOf(false) } // 控制令牌过期对话框的状态

    LaunchedEffect(Unit) {
        fetchNewBooks(
            accessToken,
            refreshToken,
            onDismiss,
            navController,
            books,
            isLoading,
            errorMessage,
            context,
            tokenUtil,
            { showLoadingDialog = true }, // 显示加载对话框
            { showLoadingDialog = false }, // 隐藏加载对话框
            { showTokenExpiredDialog = true } // 显示令牌过期对话框
        )
    }

    // 显示加载对话框
    if (showLoadingDialog) {
        AlertDialog(
            onDismissRequest = { /* Do nothing */ },
            title = { Text("加载中") },
            text = { Text("正在获取新书，请稍候...") },
            confirmButton = {
                Button(onClick = { showLoadingDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 显示令牌过期对话框
    if (showTokenExpiredDialog) {
        AlertDialog(
            onDismissRequest = { showTokenExpiredDialog = false },
            title = { Text("令牌过期") },
            text = { Text("您的登录令牌已过期，请重新登录。") },
            confirmButton = {
                Button(onClick = {
                    // 清除本地保存的 access_token 和 refresh_token
                    tokenUtil.clearTokens(context)
                    // 跳转到登录注册界面
                    navController.navigate("login") // 跳转到登录界面
                    showTokenExpiredDialog = false
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                Button(onClick = { showTokenExpiredDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("新书展示", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(books.value) { book ->
                    NewBookItem(
                        navController = navController,
                        id = book.id,
                        title = book.book_title,
                        author = book.author,
                        tags = TagUtil().converseTags(listOf(book.tags)),
                        imageUrl = "https:${book.img_path}"
                    )
                }
            }
        }
    }
}

private suspend fun fetchNewBooks(
    accessToken: String?,
    refreshToken: String,
    onDismiss: () -> Unit,
    navController: NavController,
    books: MutableState<List<Book>>,
    isLoading: MutableState<Boolean>,
    errorMessage: MutableState<String?>,
    context: Context,
    tokenUtil: TokenUtil,
    showLoading: () -> Unit, // 显示加载对话框的函数
    hideLoading: () -> Unit, // 隐藏加载对话框的函数
    showTokenExpired: () -> Unit // 显示令牌过期对话框的函数
) {
    showLoading() // 显示加载对话框
    try {
        val response: Response<ApiResponse<List<Book>>> =
            RetrofitClient.apiService.getNewBooks(
                accessToken ?: "",
                page = 1, size = 8
            )

        if (response.isSuccessful) {
            val apiResponse = response.body()
            if (apiResponse?.code == 200) {
                books.value = apiResponse.data
            } else {
                errorMessage.value = "Error: ${apiResponse?.message}"
            }
        } else if (response.code() == 401) { // 401 Unauthorized
            // 尝试使用 refresh_token 获取新的 access_token
            val newAccessToken = RefreshTokenUtil().refreshAccessToken(refreshToken, context, navController, tokenUtil)
            if (newAccessToken != null) {
                // 重新尝试获取新书
                fetchNewBooks(newAccessToken, refreshToken, onDismiss, navController, books, isLoading, errorMessage, context, tokenUtil, showLoading, hideLoading, showTokenExpired)
            } else {
                // 如果 refreshToken 也无法访问，显示令牌过期对话框
                showTokenExpired()
            }
        } else {
            errorMessage.value = "Error: ${response.code()}"
        }
    } catch (e: Exception) {
        errorMessage.value = "Error: ${e.message}"
    } finally {
        hideLoading() // 隐藏加载对话框
        isLoading.value = false
    }
}

