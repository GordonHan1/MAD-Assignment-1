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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

    // Pagination state
    var currentPage by remember { mutableStateOf(1) }
    var totalPages by remember { mutableStateOf(1) }

    // User input for jumping to a page
    var pageInput by remember { mutableStateOf("") }

    // Fetch movies whenever 'selectedFilter' OR 'currentPage' changes
    LaunchedEffect(selectedFilter, currentPage) {
        isLoading = true
        withContext(Dispatchers.IO) {
            try {
                val response = when (selectedFilter) {
                    MovieListType.POPULAR ->
                        RetrofitClient.instance.getPopularMovies(
                            apiKey = "24f4591904aa6cb41814de8604cb5e04",
                            page = currentPage
                        )
                    MovieListType.TOP_RATED ->
                        RetrofitClient.instance.getTopRatedMovies(
                            apiKey = "24f4591904aa6cb41814de8604cb5e04",
                            page = currentPage
                        )
                    MovieListType.NOW_PLAYING ->
                        RetrofitClient.instance.getNowPlayingMovies(
                            apiKey = "24f4591904aa6cb41814de8604cb5e04",
                            page = currentPage
                        )
                    MovieListType.UPCOMING ->
                        RetrofitClient.instance.getUpcomingMovies(
                            apiKey = "24f4591904aa6cb41814de8604cb5e04",
                            page = currentPage
                        )
                }

                movies = response.results
                totalPages = response.total_pages
            } catch (e: Exception) {
                // Handle error appropriately
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
                        .height(40.dp)  // Adjust height as needed for a pill look
                ) {
                    Text(
                        text = "Now showing: $selectedOption",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal
                    )
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
                                // Reset to page 1 whenever filter changes
                                currentPage = 1
                                pageInput = "" // Clear the text field
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                // Loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading...")
                }
            } else {
                // Movie list (takes up remaining space)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
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

                // ========== PAGINATION CONTROLS AT THE BOTTOM ==========
                PaginationBar(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    onPageChange = { newPage ->
                        currentPage = newPage
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    maxVisiblePages = 7  // or however many you want in the row
                )
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


@Composable
fun PaginationBar(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxVisiblePages: Int = 5
) {
    // State controlling whether to show the dialog for jumping to a page
    var showPageJumpDialog by remember { mutableStateOf(false) }

    // Generate a sequence of page “items” (1, 2, …, 9, 10, etc.) plus placeholders (-1 for “…”)
    val pagesToShow = remember(currentPage, totalPages) {
        generatePagesList(currentPage, totalPages, maxVisiblePages)
    }

    // -- Jump-to-page Dialog (or popup) --
    if (showPageJumpDialog) {
        PageJumpDialog(
            totalPages = totalPages,
            onDismiss = { showPageJumpDialog = false },
            onPageChosen = {
                onPageChange(it)
                showPageJumpDialog = false
            }
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Prev button
        IconButton(
            onClick = { if (currentPage > 1) onPageChange(currentPage - 1) },
            enabled = (currentPage > 1)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Previous"
            )
        }

        // Page “chips”
        pagesToShow.forEach { pageItem ->
            when {
                // A real page number
                pageItem >= 1 -> {
                    OutlinedButton(
                        onClick = { onPageChange(pageItem) },
                        // highlight the currently active page
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (pageItem == currentPage) Color(0xFFEDE7F6) else Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = pageItem.toString(),
                            color = if (pageItem == currentPage) Color(0xFF6200EE) else Color.Black
                        )
                    }
                }
                // The “...” placeholder (we use pageItem == -1 to indicate the placeholder)
                pageItem == -1 -> {
                    TextButton(
                        onClick = { showPageJumpDialog = true },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text("...")
                    }
                }
            }
        }

        // Next button
        IconButton(
            onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
            enabled = (currentPage < totalPages)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next"
            )
        }
    }
}

/**
 * Helper function: generates a list of integers representing page numbers + “-1” for “...”
 * Example output: [1, 2, -1, 8, 9, 10] if totalPages=10, currentPage ~ middle, etc.
 */
fun generatePagesList(
    currentPage: Int,
    totalPages: Int,
    maxVisiblePages: Int
): List<Int> {
    // If total pages are small enough, just show them all
    if (totalPages <= maxVisiblePages) {
        return (1..totalPages).toList()
    }

    val pages = mutableListOf<Int>()

    val firstPage = 1
    val lastPage = totalPages

    // Always add the first page
    pages.add(firstPage)

    // Decide how many pages to show around the current page
    val halfRange = (maxVisiblePages - 3) / 2  // -3 for first, last, and maybe the "..."

    // Lower bound in the middle
    val start = (currentPage - halfRange).coerceAtLeast(firstPage + 1)
    // Upper bound in the middle
    val end = (start + (maxVisiblePages - 3)).coerceAtMost(lastPage - 1)

    // If the gap between the first page and the start is > 1, add “...”
    if (start > firstPage + 1) {
        pages.add(-1)  // “...”
    }

    // Add the middle range
    for (page in start..end) {
        pages.add(page)
    }

    // If the gap between the end and the last page is > 1, add “...”
    if (end < lastPage - 1) {
        pages.add(-1)  // “...”
    }

    // Always add the last page
    pages.add(lastPage)

    return pages
}

@Composable
fun PageJumpDialog(
    totalPages: Int,
    onDismiss: () -> Unit,
    onPageChosen: (Int) -> Unit
) {
    var textValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Go to Page") },
        text = {
            Column {
                Text("Enter a page between 1 and $totalPages:")
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val number = textValue.toIntOrNull()
                    if (number != null && number in 1..totalPages) {
                        onPageChosen(number)
                    } else {
                        // Optionally handle invalid inputs here
                        onDismiss()
                    }
                }
            ) {
                Text("Go")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}