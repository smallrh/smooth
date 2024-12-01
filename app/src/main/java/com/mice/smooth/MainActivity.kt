package com.mice.smooth

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.navigation.compose.currentBackStackEntryAsState
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
import com.mice.smooth.util.setKeyEventListener
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmoothTheme {
                var showRankings by remember { mutableStateOf(false) }
                var showHighRated by remember { mutableStateOf(false) }
                var showNewBooks by remember { mutableStateOf(false) }
                var selectedTab by remember { mutableIntStateOf(0) }
                var isInBookDetailScreen by remember { mutableStateOf(false) }

                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()
                val pagerState = rememberPagerState()

                val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                val token = sharedPreferences.getString("refresh_token", null)
                val startDestination = if (token != null) "tab0" else "login"

                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                val showBars = currentRoute != "login" && !isInBookDetailScreen

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
                            val currentDestination = navController.currentDestination?.route
                            if (currentDestination in listOf("tab0", "tab1", "tab2", "tab3")) {
                                finish() // 关闭当前 Activity
                            } else {
                                navController.popBackStack()
                            }
                            true
                        }
                        else -> false
                    }
                }

                Scaffold(
                    bottomBar = {
                        if (showBars) {
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
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginRegistrationScreen(onLoginSuccess = {
                                sharedPreferences.edit().putString("refresh_token", "dummy_token").apply()
                                navController.navigate("tab0") {
                                    popUpTo("login") { inclusive = true }
                                }
                            })
                        }
                        composable("tab0") {
                            NovelHomePage(
                                onOpenRankings = { showRankings = true },
                                onOpenHighRated = { showHighRated = true },
                                onOpenNewBooks = { showNewBooks = true },
                            )
                        }
                        composable("tab1") {
                            Text("书架页面内容")
                        }
                        composable("tab2") {
                            Text("书城页面内容")
                        }
                        composable("tab3") {
                            MyProfilePage()
                        }
                        composable("bookDetail/{id}/{title}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                            val title = backStackEntry.arguments?.getString("title").toString()
                            DisposableEffect(Unit) {
                                isInBookDetailScreen = true
                                showNewBooks= false
                                showHighRated= false
                                showRankings= false
                                onDispose {
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
                    RankingsModal(onDismiss = { showRankings = false }, navController = navController)
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

