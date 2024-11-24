package com.mice.smooth.home.modal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mice.smooth.api.ApiResponse
import com.mice.smooth.api.Book
import com.mice.smooth.api.RetrofitClient
import com.mice.smooth.home.NewBookItem
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBooksModal(onDismiss: () -> Unit, navController: NavController) {
    val books = remember { mutableStateOf<List<Book>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        try {
            val response: Response<ApiResponse<List<Book>>> = RetrofitClient.apiService.getNewBooks(page = 1, size = 8)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.code == 200) {
                    books.value = apiResponse.data
                } else {
                    errorMessage.value = "Error: ${apiResponse?.message}"
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
                        tags = book.tags,
                        imageUrl = "https:${book.img_path}"
                    )
                }
            }
        }
    }
}
