package com.mice.smooth.home.screen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.mice.smooth.api.ApiResponse
import com.mice.smooth.api.Chapter
import com.mice.smooth.api.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Response

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    navController: NavController,
    name: String,
    bookId: Int,
    pagerState: PagerState // 传递 pagerState
) {
    val coroutineScope = rememberCoroutineScope()
    val chapters = remember { mutableStateOf<List<Chapter>>(emptyList()) }
    val context = LocalContext.current
    val activity = remember { context as? ComponentActivity }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    LaunchedEffect(bookId) {
        try {
            val response: Response<ApiResponse<List<Chapter>>> =
                RetrofitClient.apiService.getBookDetail(id = bookId)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.code == 200) {
                    chapters.value = apiResponse.data
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

    ModalNavigationDrawer(
        drawerContent = {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "目录",
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        )
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.close()
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close drawer")
                        }
                    }
                    LazyColumn {
                        items(chapters.value) { chapter ->
                            Text(
                                text = chapter.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .clickable {
                                        coroutineScope.launch {
                                            pagerState.scrollToPage(chapters.value.indexOf(chapter))
                                            drawerState.close()
                                        }
                                    }
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    modifier = Modifier.height(32.dp)
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .clickable {
                        coroutineScope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    }
            ) {
                if (isLoading.value) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (errorMessage.value != null) {
                    Text(
                        text = "加载失败: ${errorMessage.value}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    HorizontalPager(
                        count = chapters.value.size,
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val chapter = chapters.value[page]
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .background(Color.White, shape = RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        ) {
                            item {
                                Text(
                                    text = chapter.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            item {
                                Text(
                                    text = chapter.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}