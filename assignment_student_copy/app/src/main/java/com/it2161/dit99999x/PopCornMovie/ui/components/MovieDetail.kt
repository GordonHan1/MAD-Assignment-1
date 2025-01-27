@file:OptIn(ExperimentalMaterial3Api::class)

package com.it2161.dit99999x.PopCornMovie.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.util.Date
import kotlin.math.abs

@Composable
fun MovieDetailScreen(navController: NavController, movieName: String, moviePoster: Bitmap, movieDetails: String, comments: List<Comment>) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back Arrow")
                        }
                        Text(movieName, textAlign = TextAlign.Center, modifier = Modifier.weight(2f))
                        Box(contentAlignment = Alignment.CenterEnd) {
                            IconButton(onClick = { showMenu = !showMenu }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Overflow Menu")
                                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                    DropdownMenuItem(
                                        text = { Text("Add comments") },
                                        onClick = {
                                            navController.navigate("add_comment_screen")
                                            showMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Image(
                    bitmap = moviePoster.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = movieName, fontSize = 24.sp)
                    Text(text = movieDetails, fontSize = 16.sp)
                }
            }

            Text(
                text = "Comments",
                modifier = Modifier.padding(16.dp),
                fontSize = 20.sp,
                color = Color.Gray
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(comments.sortedByDescending { it.timestamp }) { comment ->
                    CommentItem(comment) { clickedComment ->
                        navController.navigate("view_comment_screen/${clickedComment.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment, onCommentClick: (Comment) -> Unit) {
    val currentTime = Date()
    val timeDifference = abs(currentTime.time - comment.timestamp.time) / (1000 * 60 * 60)
    val timeAgoText = if (timeDifference < 24) "$timeDifference hrs ago" else "${timeDifference / 24} days ago"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onCommentClick(comment) }
    ) {
        Text(text = comment.userName, fontSize = 16.sp, color = Color.Black)
        Text(text = comment.content, fontSize = 14.sp, color = Color.Gray)
        Text(text = timeAgoText, fontSize = 12.sp, color = Color.Gray)
    }
}

// Sample data model for Comment
data class Comment(val id: String, val userName: String, val content: String, val timestamp: Date)

@Preview(showBackground = true)
@Composable
fun MovieDetailScreenPreview() {
    // Mock NavController
    val navController = rememberNavController()

    // Sample data for the movie
    val sampleMovieName = "Inception"
    val sampleMovieDetails = "A mind-bending thriller by Christopher Nolan."

    // Placeholder Bitmap for the movie poster
    val sampleBitmap = Bitmap.createBitmap(150, 200, Bitmap.Config.ARGB_8888)

    // Sample comments
    val sampleComments = listOf(
        Comment("1", "User1", "Amazing movie!", Date(Date().time - 3600 * 1000)), // 1 hour ago
        Comment("2", "User2", "Loved the visuals!", Date(Date().time - 7200 * 1000)), // 2 hours ago
        Comment("3", "User3", "Great story!", Date(Date().time - 172800 * 1000)) // 2 days ago
    )

    // Preview the MovieDetailScreen composable
    MovieDetailScreen(
        navController = navController,
        movieName = sampleMovieName,
        moviePoster = sampleBitmap,
        movieDetails = sampleMovieDetails,
        comments = sampleComments
    )
}

