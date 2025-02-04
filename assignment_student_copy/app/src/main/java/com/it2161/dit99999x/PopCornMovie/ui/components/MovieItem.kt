package com.it2161.dit99999x.PopCornMovie.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.it2161.dit99999x.PopCornMovie.ui.theme.Roboto

@Composable
fun MovieItem(
    movieId: Int,
    imageFileName: String,
    title: String,
    synopsis: String,
    rating: Float,
    releaseDate: String,
    navController: NavController
) {
    val isDark = isSystemInDarkTheme()
    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                navController.navigate("movie_detail_screen/$movieId")
            }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                clip = true
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Movie Poster with Circular Rating Overlay
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .size(width = 100.dp, height = 150.dp)
            ) {
                // Movie Poster Image
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(getValidImageUrl(imageFileName))
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )


                // Circular Rating Indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(3.dp)
                        .size(30.dp) // Size of the circular progress bar
                        .background(Color.Black.copy(alpha = 0.7f), CircleShape) // Background for the circle
                ) {
                    // Draw the circular progress bar
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 2.3.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2
                        val centerOffset = size.width / 2

                        // Background circle
                        drawCircle(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            radius = radius,
                            center = Offset(centerOffset, centerOffset),
                            style = Stroke(strokeWidth)
                        )

                        // Progress circle
                        val sweepAngle = (rating / 10) * 360 // Convert rating to degrees
                        drawArc(
                            color = Color(0xFF24C274), // Green color for progress
                            startAngle = -90f, // Start from the top
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                            size = Size(size.width - strokeWidth, size.height - strokeWidth),
                            style = Stroke(strokeWidth)
                        )
                    }

                    // Percentage Text in the Center
                    Text(
                        text = "${(rating * 10).toInt()}%",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Movie Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Movie Title
                val titleColor = if (isDark) Color(0xFF174575) else Color(0xFF0D253F)

                Text(
                    text = title,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = titleColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Release Date
                Text(
                    text = releaseDate,
                    fontFamily = Roboto,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Synopsis
                Text(
                    text = synopsis,
                    fontFamily = Roboto,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 3,
                    lineHeight = 12.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

fun getValidImageUrl(imagePath: String?): String {
    return if (!imagePath.isNullOrEmpty()) {
        "https://image.tmdb.org/t/p/w500$imagePath"
    } else {
        "file:///android_asset/default_movie_poster.jpg" // Use a local placeholder image
    }
}