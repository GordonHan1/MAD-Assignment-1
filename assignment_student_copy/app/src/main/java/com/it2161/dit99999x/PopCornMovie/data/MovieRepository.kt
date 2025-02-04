// MovieRepository.kt
package com.it2161.dit99999x.PopCornMovie.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.it2161.dit99999x.PopCornMovie.ui.components.MovieListType
import com.it2161.dit99999x.PopCornMovie.ui.components.MovieReviewsResponse

class MovieViewerApplication : Application() {
    lateinit var database: MovieDatabase
        private set

    lateinit var repository: MovieRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = MovieDatabase.getDatabase(this)
        repository = MovieRepository(database.movieDao(), this)
    }

    companion object {
        private lateinit var instance: MovieViewerApplication

        fun getInstance(): MovieViewerApplication {
            if (!::instance.isInitialized) {
                throw IllegalStateException("MovieViewerApplication not initialized")
            }
            return instance
        }
    }
}

class MovieRepository(
    private val movieDao: MovieDao,
    private val context: Context
) {
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                )
    }

    suspend fun getMoviesByType(type: MovieListType, page: Int): MovieResponse {
        return if (isNetworkAvailable()) {
            // Fetch from API as before
            try {
                // Fetch from API as before
                val response = when (type) {
                    MovieListType.POPULAR -> RetrofitClient.instance.getPopularMovies(
                        apiKey = "24f4591904aa6cb41814de8604cb5e04",
                        page = page
                    )
                MovieListType.TOP_RATED -> RetrofitClient.instance.getTopRatedMovies(
                    apiKey = "24f4591904aa6cb41814de8604cb5e04",
                    page = page
                )
                MovieListType.NOW_PLAYING -> RetrofitClient.instance.getNowPlayingMovies(
                    apiKey = "24f4591904aa6cb41814de8604cb5e04",
                    page = page
                )
                MovieListType.UPCOMING -> RetrofitClient.instance.getUpcomingMovies(
                    apiKey = "24f4591904aa6cb41814de8604cb5e04",
                    page = page
                )
            }

            // Convert API response to entities and store in database
                val movieEntities = response.results.map { movie ->
                    MovieEntity(
                        id = movie.id,
                        title = movie.title,
                        overview = movie.overview,
                        posterPath = movie.poster_path,
                        releaseDate = movie.release_date,
                        runtime = 0, // We'll update this later when details are fetched
                        genres = "",
                        voteAverage = movie.vote_average ?: 0.0F,
                        revenue = 0L,
                        page = page
                    )
                }

                if (movieEntities.isNotEmpty()) {
                    movieDao.insertMovies(movieEntities)
                    Log.d("MovieData", "Stored ${movieEntities.size} movies in database")
                }

                // Return the unfiltered response
                response
        } catch (e: Exception) {
            // If API call fails, fall back to database
            val cachedMovies = movieDao.getMoviesForPage(page)
            MovieResponse(
                page = page,
                results = cachedMovies.map { entity ->
                    Movie(
                        id = entity.id,
                        title = entity.title,
                        overview = entity.overview,
                        poster_path = entity.posterPath ?: "",
                        release_date = entity.releaseDate,
                        vote_average = entity.voteAverage
                    )
                },
                total_pages = movieDao.getMaxPage()
            )
        }
    } else {
            // If offline, fetch from database
            val cachedMovies = movieDao.getMoviesForPage(page)
            MovieResponse(
                page = page,
                results = cachedMovies.map { entity ->
                    Movie(
                        id = entity.id,
                        title = entity.title,
                        overview = entity.overview,
                        poster_path = entity.posterPath ?: "",
                        release_date = entity.releaseDate,
                        vote_average = entity.voteAverage
                    )
                },
                total_pages = movieDao.getMaxPage() // Get actual number of cached pages
            )
        }
    }
    suspend fun getMovieDetails(movieId: Int): MovieDetailsResponse {
        return if (isNetworkAvailable()) {
            val details = RetrofitClient.instance.getMovieDetails(movieId, apiKey = "24f4591904aa6cb41814de8604cb5e04")

            val movieEntity = details.runtime?.let {
                MovieEntity(
                    id = movieId,
                    title = details.title,
                    overview = details.overview,
                    posterPath = details.poster_path,
                    releaseDate = details.releaseDate,
                    runtime = it,
                    genres = details.genres.joinToString { it.name },
                    voteAverage = details.voteAverage,
                    revenue = details.revenue,
                    page = movieDao.getMoviePage(movieId) ?: 1 // Get existing page or default to 1
                )
            }
            if (movieEntity != null) {
                movieDao.insertMovieDetails(movieEntity)
            }

            details
        } else {
            // Fetch from local database if offline
            val cachedMovie = movieDao.getMovieDetails(movieId)
            if (cachedMovie != null) {
                MovieDetailsResponse(
                    adult = false,
                    genres = cachedMovie.genres?.split(",")?.map { Genre(0, it) } ?: emptyList(),
                    originalLanguage = "en",
                    releaseDate = cachedMovie.releaseDate,
                    runtime = cachedMovie.runtime,
                    voteCount = 0,
                    title = cachedMovie.title,
                    poster_path = cachedMovie.posterPath ?: "",
                    voteAverage = cachedMovie.voteAverage,
                    overview = cachedMovie.overview,
                    revenue = cachedMovie.revenue
                )
            } else {
                throw Exception("No cached data available for movie ID: $movieId")
            }
        }
    }

    suspend fun getMovieReviews(movieId: Int): MovieReviewsResponse {
        return RetrofitClient.instance.getMovieReviews(
            movieId = movieId,
            apiKey = "24f4591904aa6cb41814de8604cb5e04")
    }

    suspend fun searchMovies(query: String, page: Int = 1): MovieResponse {
        return RetrofitClient.instance.searchMovies(
            apiKey = "24f4591904aa6cb41814de8604cb5e04",
            query = query,
            page = page
        )
    }

    suspend fun insertMovieDetails(details: MovieDetailsResponse, movieId: Int) {
        val movieEntity = MovieEntity(
            id = movieId,
            title = details.title,
            overview = details.overview,
            posterPath = details.poster_path ?: "",
            releaseDate = details.releaseDate ?: "",
            runtime = details.runtime ?: 0,
            genres = details.genres.joinToString { it.name },
            voteAverage = details.voteAverage ?: 0.0F,
            revenue = details.revenue ?: 0L,
            page = movieDao.getMoviePage(movieId) ?: 1 // Get existing page or default to 1
        )
        movieDao.insertMovieDetails(movieEntity)
    }

    suspend fun getSimilarMovies(movieId: Int): MovieResponse {
        return RetrofitClient.instance.getSimilarMovies(
            movieId = movieId,
            apiKey = "24f4591904aa6cb41814de8604cb5e04"
        )
    }

    suspend fun cacheMovieWhenVisible(movie: Movie, page: Int) {
        // Only cache if we don't already have this movie
        if (movieDao.getMovieDetails(movie.id) == null) {
            // Fetch additional details only when movie becomes visible
            val details = try {
                RetrofitClient.instance.getMovieDetails(
                    movie.id,
                    apiKey = "24f4591904aa6cb41814de8604cb5e04"
                )
            } catch (e: Exception) {
                null
            }

            val movieEntity = MovieEntity(
                id = movie.id,
                title = movie.title,
                overview = movie.overview,
                posterPath = movie.poster_path,
                releaseDate = movie.release_date,
                page = page, // Store which page this movie belongs to
                runtime = details?.runtime ?: 0,
                genres = details?.genres?.joinToString { it.name } ?: "Unknown",
                voteAverage = details?.voteAverage ?: 0.0F,
                revenue = details?.revenue ?: 0L
            )
            movieDao.insertMovieDetails(movieEntity)
        }
    }


    suspend fun getFavorites(): List<Movie> {
        return movieDao.getAllFavorites().map { entity ->
            Movie(
                id = entity.movieId,
                title = entity.title,
                overview = entity.overview,
                poster_path = entity.posterPath ?: "",
                release_date = entity.releaseDate,
                vote_average = entity.voteAverage
            )
        }
    }

    suspend fun addToFavorites(movie: Movie) {
        movieDao.insertFavorite(
            FavoriteEntity(
                movieId = movie.id,
                title = movie.title,
                overview = movie.overview,
                posterPath = movie.poster_path,
                releaseDate = movie.release_date,
                voteAverage = movie.vote_average ?: 0f
            )
        )
    }

    suspend fun removeFromFavorites(movieId: Int) {
        movieDao.removeFavorite(movieId)
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        return movieDao.isFavorite(movieId)
    }
}