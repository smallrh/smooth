package com.mice.smooth.home.page

import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovelHomePage(
    onOpenRankings: () -> Unit,
    onOpenHighRated: () -> Unit,
    onOpenNewBooks: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smooth,圆你读书梦") },
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item { RankingsSummary(onOpenRankings) }
            item { HighRatedWorksSummary(onOpenHighRated) }
            item { NewBooksSummary(onOpenNewBooks) }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingsSummary(onOpenRankings: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = onOpenRankings
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("榜单", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("查看热门、新书和好评榜单", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighRatedWorksSummary(onOpenHighRated: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = onOpenHighRated
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("高分巨作", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("发现最受欢迎的高分作品", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBooksSummary(onOpenNewBooks: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = onOpenNewBooks
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("新书展示", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("浏览最新上架的图书", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
