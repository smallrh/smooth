package com.mice.smooth.home.modal


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingsModal(onDismiss: () -> Unit) {
    var selectedRanking by remember { mutableStateOf(0) }
    val rankings = listOf("热门榜", "新书榜", "好评榜")
    val books = remember { List(8) { index -> "书籍 ${index + 1}" to "作者 ${index + 1}" } }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("榜单", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            ScrollableTabRow(selectedTabIndex = selectedRanking) {
                rankings.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedRanking == index,
                        onClick = { selectedRanking = index },
                        text = { Text(title) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(books) { (title, author) ->
                    ListItem(
                        headlineContent = { Text(title) },
                        supportingContent = { Text(author) }
                    )
                }
            }
        }
    }
}

