package com.mice.smooth.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.mice.smooth.api.Book

@Composable
fun BookItem(book: Book,navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("bookDetail/${book.id}/${book.book_title}")
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = "https:" +book.img_path),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(end = 8.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = book.book_title, style = MaterialTheme.typography.bodyLarge)
            Text(text = "作者: ${book.author}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "状态: ${book.status}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun NewBookItem(navController: NavController,
                id: Int,
                title: String,
                author: String,
                tags: String,
                imageUrl: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable {
                navController.navigate("bookDetail/$id/$title")
            }
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp, 120.dp)
                    .clip(MaterialTheme.shapes.small)
            ) {
                val painter = rememberAsyncImagePainter(
                    model = imageUrl
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (painter.state is AsyncImagePainter.State.Error) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Text(
                            text = "暂无图片封面",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
            ) {
                Text(text = title, style = MaterialTheme.typography.titleSmall)
                Text(text = tags, style = MaterialTheme.typography.bodySmall)
                Text(text = author, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

