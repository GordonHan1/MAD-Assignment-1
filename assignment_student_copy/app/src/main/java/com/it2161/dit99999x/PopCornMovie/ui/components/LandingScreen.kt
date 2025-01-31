package com.it2161.dit99999x.PopCornMovie.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.it2161.dit99999x.PopCornMovie.R
import com.it2161.dit99999x.PopCornMovie.data.Movie
import com.it2161.dit99999x.PopCornMovie.data.RetrofitClient
import com.it2161.dit99999x.PopCornMovie.ui.screens.LandingPageViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.it2161.dit99999x.PopCornMovie.ui.theme.Roboto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max


enum class MovieListType {
    POPULAR, TOP_RATED, NOW_PLAYING, UPCOMING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingPage(
    navController: NavController,
    viewModel: LandingPageViewModel = viewModel()
) {
    var scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    // Observe the view model’s state
    val movies = viewModel.movies
    val isLoading = viewModel.isLoading
    val currentPage = viewModel.currentPage
    val totalPages = viewModel.totalPages
    val selectedFilter = viewModel.selectedFilter

    // The rest of your local states...
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Popular") }
    val options = listOf("Popular", "Top Rated", "Upcoming", "Now Playing")

    val lazyListState = rememberLazyListState()

    LaunchedEffect(viewModel.currentPage) {
        lazyListState.scrollToItem(0)
        scrollBehavior.state.heightOffset = 0f
    }
    // 1) If you want to fetch immediately when the composable is first displayed:
    LaunchedEffect(Unit, selectedFilter) {
        viewModel.fetchMovies()
    }

    // 2) When user changes the filter from the dropdown
    //    we'll call viewModel.updateFilter(...) in onClick

    // 3) When user changes the page (onPageChange), we'll call viewModel.setPage(newPage)

    Scaffold(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        // topBar and bottomBar as before
        topBar = {
            TopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary),
            title = {
                Image(
                    painter = painterResource(id = R.drawable.tmdblogo1),
                    contentDescription = "TMDb Logo",
                    modifier = Modifier.size(150.dp) // Adjust size as needed
                )
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF0D253F),
                scrolledContainerColor = Color(0xFF0D253F),
            )
        )
        },
        bottomBar = { BottomAppBar(navController) }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        } else {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 0.dp)
                    .androidScrollbar(state = lazyListState)
            ) {

                item {
                    PaginationBar(
                        currentPage = currentPage,
                        totalPages = totalPages,
                        onPageChange = { newPage ->
                            viewModel.setPage(newPage)
                            // Also if you want to scroll to top:
                            // Make sure to do this inside a coroutine:
                            // rememberCoroutineScope().launch {
                            //     lazyListState.animateScrollToItem(0)
                            // }
                        }
                    )
                }
                // Dropdown item
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            shape = RoundedCornerShape(50),
                            /* border, colors, etc. */
                        ) {
                            Text("Now showing: $selectedOption")
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
                                        val filterType = when (option) {
                                            "Popular" -> MovieListType.POPULAR
                                            "Top Rated" -> MovieListType.TOP_RATED
                                            "Now Playing" -> MovieListType.NOW_PLAYING
                                            "Upcoming" -> MovieListType.UPCOMING
                                            else -> MovieListType.POPULAR
                                        }
                                        viewModel.updateFilter(filterType)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Movie items
                items(movies) { movie ->
                    MovieItem(
                        movieId = movie.id,
                        imageFileName = movie.poster_path ?: "",
                        title = movie.title,
                        synopsis = movie.overview,
                        rating = movie.vote_average,
                        releaseDate = movie.release_date,
                        navController = navController
                    )
                }

                // Pagination bar
                item {
                    PaginationBar(
                        currentPage = currentPage,
                        totalPages = totalPages,
                        onPageChange = { newPage ->
                            viewModel.setPage(newPage)
                            // Also if you want to scroll to top:
                            // Make sure to do this inside a coroutine:
                            // rememberCoroutineScope().launch {
                            //     lazyListState.animateScrollToItem(0)
                            // }
                        }
                    )
                }
            }
        }
    }
}

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
                            .data("https://image.tmdb.org/t/p/w500$imageFileName")
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

/** Same PaginationBar and related functions as before. **/
@Composable
fun PaginationBar(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayedPages = (currentPage - 2..currentPage + 2).filter { it in 1..totalPages }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // First Page Button
        IconButton(
            onClick = { onPageChange(1) },
            enabled = currentPage > 1
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_keyboard_double_arrow_left_24), // Use painterResource for vector asset
                contentDescription = "First Page"
            )
        }

        // Previous Page Button
        IconButton(
            onClick = { if (currentPage > 1) onPageChange(currentPage - 1) },
            enabled = currentPage > 1
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Previous"
            )
        }

        // Page Numbers
        displayedPages.forEach { page ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(12.dp)) // Squircle shape
                    .background(if (page == currentPage) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { onPageChange(page) }
                    .padding(horizontal = 5.dp, vertical = 8.dp)
            ) {
                Text(
                    text = page.toString(),
                    fontWeight = if (page == currentPage) FontWeight.Bold else FontWeight.Normal,
                    color = if (page == currentPage) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
        }

        // Next Page Button
        IconButton(
            onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next"
            )
        }

        // Last Page Button
        IconButton(
            onClick = { onPageChange(totalPages) },
            enabled = currentPage < totalPages
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_keyboard_double_arrow_right_24),
                contentDescription = "Last Page"
            )
        }
    }
}


@Composable
fun Modifier.androidScrollbar(
    state: LazyListState,
    color: Color = Color(0xFF666666),
    minThumbHeight: Dp = 24.dp
): Modifier {
    val previousProgress = remember { mutableStateOf(0f) }
    val previousThumbHeight = remember { mutableStateOf(0f) }

    return drawWithContent {
        drawContent()

        if (state.layoutInfo.totalItemsCount > 0) {
            // Extract values to reduce redundant computations
            val layoutInfo = state.layoutInfo
            val firstVisibleItemIndex = state.firstVisibleItemIndex
            val visibleItemsCount = layoutInfo.visibleItemsInfo.size
            val totalItemsCount = layoutInfo.totalItemsCount

            if (visibleItemsCount == 0 || totalItemsCount <= visibleItemsCount) return@drawWithContent

            val firstVisibleItemOffset = state.firstVisibleItemScrollOffset
            val itemSize = layoutInfo.visibleItemsInfo.firstOrNull()?.size?.toFloat() ?: return@drawWithContent

            // Precompute constants
            val totalScrollableDistance = (totalItemsCount - visibleItemsCount).coerceAtLeast(1).toFloat()
            val normalizedOffset = firstVisibleItemOffset / itemSize
            val exactProgress = (firstVisibleItemIndex + normalizedOffset) / totalScrollableDistance

            // Smooth progress and avoid sudden jumps
            val smoothedProgress = lerp(previousProgress.value, exactProgress.coerceIn(0f, 1f), 0.15f)
            previousProgress.value = smoothedProgress

            // Compute raw scrollbar height and apply smoothing
            val rawThumbHeight = (visibleItemsCount.toFloat() / totalItemsCount.toFloat()) * size.height
            val smoothThumbHeight = lerp(previousThumbHeight.value, rawThumbHeight.coerceAtLeast(minThumbHeight.toPx()), 0.15f)
            previousThumbHeight.value = smoothThumbHeight

            // Compute final scrollbar position
            val scrollbarOffsetY = (size.height - smoothThumbHeight) * smoothedProgress

            // Draw scrollbar
            drawRect(
                color = color,
                topLeft = Offset(size.width - 4.dp.toPx(), scrollbarOffsetY),
                size = Size(4.dp.toPx(), smoothThumbHeight),
                alpha = if (state.isScrollInProgress) 0.8f else 0.6f
            )
        }
    }
}

// Linear interpolation function
fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}


