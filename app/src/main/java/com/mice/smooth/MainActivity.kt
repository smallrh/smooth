package com.mice.smooth

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.mice.smooth.home.modal.HighRatedWorksModal
import com.mice.smooth.home.modal.NewBooksModal
import com.mice.smooth.home.modal.RankingsModal
import com.mice.smooth.home.page.MyProfilePage
import com.mice.smooth.home.page.NovelHomePage
import com.mice.smooth.home.screen.BookDetailScreen
import com.mice.smooth.login.LoginRegistrationScreen
import com.mice.smooth.ui.theme.SmoothTheme
import com.mice.smooth.util.removeKeyEventListener
import com.mice.smooth.util.setKeyEventListener
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmoothTheme {
                var isUserLoggedIn by remember { mutableStateOf(false) }
                var showRankings by remember { mutableStateOf(false) }
                var showHighRated by remember { mutableStateOf(false) }
                var showNewBooks by remember { mutableStateOf(false) }
                var selectedTab by remember { mutableIntStateOf(0) }
                var showBottomBar by remember { mutableStateOf(true) }
                var showTopBar by remember { mutableStateOf(true) }
                var isInBookDetailScreen by remember { mutableStateOf(false) }

                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()
                val pagerState = rememberPagerState()

                val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                val token = sharedPreferences.getString("refresh_token", null)
                isUserLoggedIn = token != null // 如果 Token 存在，则用户已登录


                // 设置按键事件监听器
                setKeyEventListener { event ->
                    when (event.keyCode) {
                        KeyEvent.KEYCODE_VOLUME_UP -> {
                            if (isInBookDetailScreen) {
                                coroutineScope.launch {
                                    if (pagerState.currentPage > 0) {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                                true
                            } else {
                                false
                            }
                        }
                        KeyEvent.KEYCODE_VOLUME_DOWN -> {
                            if (isInBookDetailScreen) {
                                coroutineScope.launch {
                                    if (pagerState.currentPage < pagerState.pageCount - 1) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                                true
                            } else {
                                false
                            }
                        }
                        KeyEvent.KEYCODE_BACK -> {
                            // 检查当前导航目标是否在 tab0 到 tab3 之间
                            val currentDestination = navController.currentDestination?.route
                            if (currentDestination in listOf("tab0", "tab1", "tab2", "tab3")) {
                                // 如果在这些选项卡中，关闭应用
                                finish() // 关闭当前 Activity
                            } else {
                                // 否则返回上一级
                                navController.popBackStack()
                            }
                            true
                        }
                        else -> false
                    }
                }

                if (!isUserLoggedIn) {
                    LoginRegistrationScreen(onLoginSuccess = {
                        isUserLoggedIn = true
                        // 登录成功后导航到首页
                        navController.navigate("tab0") // 确保 "tab0" 是有效的目标
                    })
                } else {
                    Scaffold(
                        topBar = {
                            if (showTopBar) {
                                TopAppBar(
                                    title = { Text("小说 App") },
                                    actions = {
                                        IconButton(onClick = { /* TODO: 实现搜索功能 */ }) {
                                            Icon(Icons.Default.Search, contentDescription = "搜索")
                                        }
                                        IconButton(onClick = { /* TODO: 实现历史记录功能 */ }) {
                                            Icon(Icons.Default.History, contentDescription = "历史记录")
                                        }
                                    }
                                )
                            }
                        },
                        bottomBar = {
                            if (showBottomBar) {
                                NavigationBar {
                                    listOf("首页", "书架", "书城", "我的").forEachIndexed { index, title ->
                                        NavigationBarItem(
                                            icon = {
                                                when (index) {
                                                    0 -> Icon(Icons.Default.Home, contentDescription = title)
                                                    1 -> Icon(Icons.Default.Book, contentDescription = title)
                                                    2 -> Icon(Icons.Default.Store, contentDescription = title)
                                                    3 -> Icon(Icons.Default.Person, contentDescription = title)
                                                }
                                            },
                                            label = { Text(title) },
                                            selected = selectedTab == index,
                                            onClick = {
                                                selectedTab = index
                                                navController.navigate("tab$index")
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "tab0"
                        ) {
                            composable("tab0") {
                                NovelHomePage(
                                    modifier = Modifier.padding(innerPadding),
                                    onOpenRankings = { showRankings = true },
                                    onOpenHighRated = { showHighRated = true },
                                    onOpenNewBooks = { showNewBooks = true },
                                    onOpenMyProfilePage = { selectedTab = 3 }
                                )
                            }
                            composable("tab1") {
                                Text("书架页面内容", modifier = Modifier.padding(innerPadding))
                            }
                            composable("tab2") {
                                Text("书城页面内容", modifier = Modifier.padding(innerPadding))
                            }
                            composable("tab3") {
                                MyProfilePage(modifier = Modifier.padding(innerPadding))
                            }
                            composable("bookDetail/{id}/{title}") { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                                val title = backStackEntry.arguments?.getString("title").toString()
                                DisposableEffect(Unit) {
                                    showBottomBar = false
                                    showTopBar = false
                                    showNewBooks = false
                                    isInBookDetailScreen = true

                                    onDispose {
                                        showBottomBar = true
                                        showTopBar = true
                                        isInBookDetailScreen = false
                                    }
                                }
                                BookDetailScreen(
                                    navController = navController,
                                    name = title,
                                    bookId = id,
                                    pagerState = pagerState
                                )
                            }
                        }
                    }

                    if (showRankings) {
                        RankingsModal(onDismiss = { showRankings = false })
                    }
                    if (showHighRated) {
                        HighRatedWorksModal(onDismiss = { showHighRated = false }, navController = navController)
                    }
                    if (showNewBooks) {
                        NewBooksModal(onDismiss = { showNewBooks = false }, navController = navController)
                    }
                }
            }
        }
    }
}