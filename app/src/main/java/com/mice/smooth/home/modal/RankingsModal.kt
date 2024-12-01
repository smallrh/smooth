package com.mice.smooth.home.modal

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.mice.smooth.api.ApiResponse
import com.mice.smooth.api.Book
import com.mice.smooth.api.RetrofitClient
import com.mice.smooth.util.RefreshTokenUtil
import com.mice.smooth.util.TagUtil
import com.mice.smooth.util.TokenUtil
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingsModal(onDismiss: () -> Unit, navController: NavController) {
    var selectedRanking by remember { mutableIntStateOf(0) }
    val rankings = listOf("热门榜", "新书榜", "好评榜")
    val booksCache = remember { mutableStateMapOf<Int, MutableList<Book>>() } // 使用 SnapshotStateMap
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val tokenUtil = TokenUtil()
    val context = LocalContext.current
    val accessToken = tokenUtil.getAccessTokenFromPreferences(context)
    val refreshToken = tokenUtil.getRefreshTokenFromPreferences(context) ?: ""

    // 保持滚动位置
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        // 一次性获取所有标签的数据
        rankings.forEachIndexed { index, _ ->
            fetchBooks(
                accessToken,
                refreshToken,
                navController,
                booksCache,
                isLoading,
                errorMessage,
                context,
                index
            )
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 使用 Row 布局将“榜单”文本和按钮放在同一行
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("榜单", style = MaterialTheme.typography.headlineMedium)
                // 添加“查看所有榜单”按钮
                Button(onClick = { /* TODO: 实现查看所有榜单的逻辑 */ }) {
                    Text("查看所有榜单")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ScrollableTabRow(selectedTabIndex = selectedRanking) {
                rankings.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedRanking == index,
                        onClick = {
                            selectedRanking = index
                            // 切换标签时检查缓存
                            if (booksCache[selectedRanking] == null) {
                                isLoading.value = true // 如果没有缓存，设置加载状态
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }

            // 防抖
            if (isLoading.value) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp), // 占位符高度
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage.value != null) {
                Text(text = "Error: ${errorMessage.value}", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(
                    state = listState, // 传递 LazyListState
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // 使用缓存中的数据
                    booksCache[selectedRanking]?.let { books ->
                        items(books) { book ->
                            BookListItem(book, navController) // 确保 BookListItem 函数已定义
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookListItem(book: Book,navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = {navController.navigate("bookDetail/${book.id}/${book.book_title}")}),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 显示书籍封面
        val painter: Painter = rememberAsyncImagePainter("https:" + book.img_path)
        Image(
            painter = painter,
            contentDescription = "Book Cover",
            modifier = Modifier
                .size(80.dp) // 设置封面大小
                .padding(end = 16.dp)
        )

        // 显示书籍信息
        Column(modifier = Modifier.weight(1f)) {
            Text(text = book.book_title, style = MaterialTheme.typography.titleMedium)
            Text(text = "作者: ${book.author}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "状态: ${book.status}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "标签: ${TagUtil().converseTags(listOf(book.tags))}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private suspend fun fetchBooks(
    accessToken: String?,
    refreshToken: String,
    navController: NavController,
    booksCache: SnapshotStateMap<Int, MutableList<Book>>,
    isLoading: MutableState<Boolean>,
    errorMessage: MutableState<String?>,
    context: Context,
    rankingIndex: Int,
) {
    isLoading.value = true
    try {
        val response: Response<ApiResponse<List<Book>>> = when (rankingIndex) {
            0 -> RetrofitClient.apiService.getHotBooks(accessToken ?: "", page = 1, size = 8)  // 获取热门书籍
            1 -> RetrofitClient.apiService.getNewBooks(accessToken ?: "", page = 1, size = 8) // 获取新书
            2 -> RetrofitClient.apiService.getGreatestBooks(accessToken ?: "", page = 1, size = 8)  // 获取好评书籍
            else -> throw IllegalArgumentException("Invalid ranking index")
        }

        if (response.isSuccessful) {
            val apiResponse = response.body()
            if (apiResponse?.code == 200) {
                // 更新缓存
                booksCache[rankingIndex] = apiResponse.data.toMutableList() // 将数据存入缓存
            } else {
                errorMessage.value = apiResponse?.message
            }
        } else if (response.code() == 401) { // 401 Unauthorized
            // 尝试使用 refresh_token 获取新的 access_token
            val newAccessToken = RefreshTokenUtil().refreshAccessToken(
                refreshToken,
                context,
                navController,
                TokenUtil()
            )
            if (newAccessToken != null) {
                // 重新尝试获取书籍
                fetchBooks(
                    newAccessToken,
                    refreshToken,
                    navController,
                    booksCache,
                    isLoading,
                    errorMessage,
                    context,
                    rankingIndex
                )
            } else {
                errorMessage.value = "令牌过期，请重新登录"
            }
        } else {
            errorMessage.value = "Error: ${response.code()}"
        }
    } catch (e: Exception) {
        errorMessage.value = "Error: ${e.message}"
    } finally {
        isLoading.value = false
    }
}