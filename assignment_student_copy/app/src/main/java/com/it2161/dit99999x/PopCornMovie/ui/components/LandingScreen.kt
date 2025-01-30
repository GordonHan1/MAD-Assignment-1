package com.it2161.dit99999x.PopCornMovie.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.it2161.dit99999x.PopCornMovie.data.Movie
import com.it2161.dit99999x.PopCornMovie.data.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.it2161.dit99999x.PopCornMovie.R
import com.it2161.dit99999x.PopCornMovie.ui.theme.Roboto

enum class MovieListType {
    POPULAR, TOP_RATED, NOW_PLAYING, UPCOMING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingPage(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Popular") }
    val options = listOf("Popular", "Top Rated", "Upcoming", "Now Playing")

    // Map selectedOption string to MovieListType
    var selectedFilter by remember { mutableStateOf(MovieListType.POPULAR) }

    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch movies whenever 'selectedFilter' changes
    LaunchedEffect(selectedFilter) {
        isLoading = true
        withContext(Dispatchers.IO) {
            try {
                val response = when (selectedFilter) {
                    MovieListType.POPULAR ->
                        RetrofitClient.instance.getPopularMovies("24f4591904aa6cb41814de8604cb5e04")
                    MovieListType.TOP_RATED ->
                        RetrofitClient.instance.getTopRatedMovies("24f4591904aa6cb41814de8604cb5e04")
                    MovieListType.NOW_PLAYING ->
                        RetrofitClient.instance.getNowPlayingMovies("24f4591904aa6cb41814de8604cb5e04")
                    MovieListType.UPCOMING ->
                        RetrofitClient.instance.getUpcomingMovies("24f4591904aa6cb41814de8604cb5e04")
                }
                movies = response.results
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // TMDb logo in the TopBar
                    Image(
                        painter = painterResource(id = R.drawable.tmdblogo1),
                        contentDescription = "TMDb Logo"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D253F) // Your dark color
                )
            )
        }
    ) { paddingValues ->
        // Main content below the TopBar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // “Now showing: X” row
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                OutlinedButton(
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(50), // Creates a "pill" shape
                    border = BorderStroke(1.dp, Color.Black),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(40.dp)  // Adjust height as needed for a pill look\
                ) {
                    Text(text = "Now showing: $selectedOption",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedOption = option
                                // Map the string to the enum
                                selectedFilter = when (option) {
                                    "Popular" -> MovieListType.POPULAR
                                    "Top Rated" -> MovieListType.TOP_RATED
                                    "Now Playing" -> MovieListType.NOW_PLAYING
                                    "Upcoming" -> MovieListType.UPCOMING
                                    else -> MovieListType.POPULAR
                                }
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Loading or Movie list
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading...")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(movies) { movie ->
                        MovieItem(
                            imageFileName = movie.poster_path,
                            title = movie.title,
                            synopsis = movie.overview,
                            rating = movie.vote_average,
                            releaseDate = movie.release_date,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(
    imageFileName: String,
    title: String,
    synopsis: String,
    rating: Float,
    releaseDate: String, // Add this parameter
    navController: NavController
) {
    OutlinedCard(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                // Navigate when the entire card is clicked
                navController.navigate("movie_detail_screen/$imageFileName/$title/$synopsis")
            }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp),
                clip = true
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Make the image fill the left side and clip its left corners
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data("https://image.tmdb.org/t/p/w500$imageFileName")
                        .crossfade(true)
                        .build()
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                    .size(width = 80.dp, height = 120.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(6.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Movie Title
                Text(
                    text = title,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 0.dp)
                )

                // Release Date in Gray Text
                Text(
                    text = releaseDate,
                    fontFamily = Roboto,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 0.dp)
                )

                // Synopsis
                Text(
                    text = synopsis,
                    fontFamily = Roboto,
                    fontSize = 12.sp,
                    maxLines = 2,
                    lineHeight = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 0.dp)
                )

                // Rating
                Text(
                    text = "Rating: $rating/10",
                    fontSize = 10.sp
                )
            }
        }
    }
}