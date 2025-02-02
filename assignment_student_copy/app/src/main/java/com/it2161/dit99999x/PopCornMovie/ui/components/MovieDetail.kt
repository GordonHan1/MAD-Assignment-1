package com.it2161.dit99999x.PopCornMovie.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.it2161.dit99999x.PopCornMovie.data.Movie
import com.it2161.dit99999x.PopCornMovie.data.MovieDetailsResponse
import com.it2161.dit99999x.PopCornMovie.data.MovieViewerApplication
import com.it2161.dit99999x.PopCornMovie.ui.components.MovieReview
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    navController: NavController,
    movieId: Int,
    viewModel: MovieDetailsViewModel = viewModel(
        factory = MovieDetailsViewModelFactory(
            MovieViewerApplication.getInstance().repository
        )
    )
) {
    val movieDetails by viewModel.movieDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val isLoadingReviews by viewModel.isLoadingReviews.collectAsState()
    val reviewsError by viewModel.reviewsError.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.fetchMovieDetails(movieId)
        viewModel.fetchSimilarMovies(movieId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = movieDetails?.title ?: "Movie Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage != null -> {
                    Text(
                        text = "Error: $errorMessage",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                movieDetails != null -> {
                    val similarMovies by viewModel.similarMovies.collectAsState()

                    MovieDetailsContent(
                        details = movieDetails!!,
                        reviews = reviews,
                        isLoadingReviews = isLoadingReviews,
                        reviewsError = reviewsError,
                        similarMovies = similarMovies,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun SimilarMoviesSection(movies: List<Movie>, navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Similar Movies",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        LazyRow(modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
            items(movies) { movie ->
                MovieCard(movie, navController)
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie, navController: NavController) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .padding(end = 8.dp)
            .clickable {
                navController.navigate("movie_detail_screen/${movie.id}")
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w185${movie.poster_path}",
            contentDescription = movie.title,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            modifier = Modifier.width(120.dp)
        )
    }
}

@Composable
fun MovieDetailsContent(
    details: MovieDetailsResponse,
    reviews: List<MovieReview>,
    isLoadingReviews: Boolean,
    reviewsError: String?,
    similarMovies: List<Movie>,
    navController: NavController
) {
    val formattedRevenue = NumberFormat.getNumberInstance(Locale.US).format(details.revenue)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data("https://image.tmdb.org/t/p/w500${details.poster_path}")
                    .crossfade(true)
                    .build()
            ),
            contentDescription = "Movie Poster",
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = details.title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Release: ${details.releaseDate} | Language: ${details.originalLanguage.uppercase()}")
                Text(text = "Genres: ${details.genres.joinToString { it.name }}")
                Text(text = "Runtime: ${details.runtime ?: 0} min | Rating: ${details.voteAverage}/10")
                Text(text = "Revenue: $$formattedRevenue")

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = details.overview,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // **INSERT SIMILAR MOVIES SECTION HERE**
        if (similarMovies.isNotEmpty()) {
            SimilarMoviesSection(movies = similarMovies, navController = navController)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // **Reviews Section**
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Reviews",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(12.dp))

                when {
                    isLoadingReviews -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    reviewsError != null -> Text(text = "Error loading reviews: $reviewsError", color = MaterialTheme.colorScheme.error)
                    reviews.isEmpty() -> Text(text = "No reviews available")
                    else -> {
                        reviews.forEachIndexed { index, review ->
                            if (index > 0) {
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                            ReviewItem(
                                author = review.author,
                                rating = review.author_details?.rating ?: 0f,
                                content = review.content,
                                date = review.createdAt,
                                avatarPath = review.author_details?.avatar_path
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItem(
    author: String,
    rating: Float,
    content: String,
    date: String?,
    avatarPath: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!avatarPath.isNullOrBlank() && !avatarPath.endsWith("null")) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://image.tmdb.org/t/p/w185${avatarPath}")
                    .crossfade(true)
                    .build(),
                contentDescription = "Author avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = author.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = author,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "★ $rating",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            date?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}