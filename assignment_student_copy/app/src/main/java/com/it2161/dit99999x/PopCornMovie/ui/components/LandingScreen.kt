// LandingScreen.kt
package com.it2161.dit99999x.PopCornMovie.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.it2161.dit99999x.PopCornMovie.R
import com.it2161.dit99999x.PopCornMovie.data.MovieViewerApplication
import kotlinx.coroutines.delay

enum class MovieListType {
    POPULAR, TOP_RATED, NOW_PLAYING, UPCOMING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingPage(
    navController: NavController,
    viewModel: LandingPageViewModel = viewModel(
        factory = LandingPageViewModelFactory(
            (LocalContext.current.applicationContext as MovieViewerApplication).repository
        )
    )
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var isDarkMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    // Observe the view model’s state
    val movies = viewModel.movies
    val isLoading = viewModel.isLoading
    val currentPage = viewModel.currentPage
    val totalPages = viewModel.totalPages
    val selectedFilter = viewModel.selectedFilter
    val context = LocalContext.current
    val repository = (context.applicationContext as MovieViewerApplication).repository
    val isOnline = remember { mutableStateOf(repository.isNetworkAvailable()) }
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

    LaunchedEffect(Unit) {
        while (true) {
            val networkStatus = repository.isNetworkAvailable()
            isOnline.value = networkStatus
            viewModel.isOffline = !networkStatus
            delay(5000) // Re-check network status every 5 seconds
        }
    }



    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                // Add the dark mode toggle in the actions slot.
                actions = {
                    IconButton(onClick = { isDarkMode = !isDarkMode }) {
                        val iconRes = if (isDarkMode) R.drawable.baseline_dark_mode_24 else R.drawable.baseline_light_mode_24
                        val contentDesc = if (isDarkMode) "Disable Dark Mode" else "Enable Dark Mode"

                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = contentDesc,
                            tint = Color.White
                        )
                    }
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

                // Dropdown item
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (isOnline.value) {
                            // Show Dropdown Button when online
                            Box {
                                OutlinedButton(
                                    onClick = { expanded = true },
                                    shape = RoundedCornerShape(50),
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


                                // Search Button

                            }
                            OutlinedButton(
                                    onClick = { isSearching = true },
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text("Search")
                            }
                        } else {
                            // Show "Offline mode" when offline
                            Text(
                                text = "Offline mode",
                                color = Color.Gray,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                    }

                    // Search Dialog
                    if (isSearching) {
                        AlertDialog(
                            onDismissRequest = { isSearching = false },
                            title = { Text("Search Movies") },
                            text = {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    label = { Text("Enter movie name") },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Text
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        viewModel.searchMovies(searchQuery)
                                        isSearching = false
                                    }
                                ) {
                                    Text("Search")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { isSearching = false }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }

                if (isOnline.value) {
                    item {
                        PaginationBar(
                            currentPage = currentPage,
                            totalPages = totalPages,
                            onPageChange = { newPage ->
                                viewModel.setPage(newPage)
                            }
                        )
                    }
                }

                // Movie items
                items(
                    items = movies,
                    key = { it.id }
                ) { movie ->
                    LaunchedEffect(movie.id) {
                        // Cache the movie when it becomes visible
                        viewModel.onMovieVisible(movie)
                    }

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
                if (isOnline.value) {
                    item {
                        PaginationBar(
                            currentPage = currentPage,
                            totalPages = totalPages,
                            onPageChange = { newPage ->
                                viewModel.setPage(newPage)
                            }
                        )
                    }
                }
            }
        }
    }
}