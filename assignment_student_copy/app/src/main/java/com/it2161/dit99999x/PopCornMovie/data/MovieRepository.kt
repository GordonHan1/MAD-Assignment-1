// MovieRepository.kt
package com.it2161.dit99999x.PopCornMovie.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
            // Fetch movies from API
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
                // Fetch additional details for each movie
                val details = try {
                    RetrofitClient.instance.getMovieDetails(movie.id, apiKey = "24f4591904aa6cb41814de8604cb5e04")
                } catch (e: Exception) {
                    null // If API call fails, fallback to defaults
                }

                MovieEntity(
                    id = movie.id,
                    title = movie.title,
                    overview = movie.overview,
                    posterPath = movie.poster_path,
                    releaseDate = movie.release_date,
                    runtime = details?.runtime ?: 0, // Default to 0 if API call fails
                    genres = details?.genres?.joinToString { it.name } ?: "Unknown", // Default if API fails
                    voteAverage = details?.voteAverage ?: 0.0F, // Default to 0.0 if missing
                    revenue = details?.revenue ?: 0L // Default to 0 if missing
                )
            }
            movieDao.insertMovies(movieEntities)

            response
        } else {
            // If offline, fetch from database
            val cachedMovies = movieDao.getAllMovies()
            MovieResponse(
                page = page,
                results = cachedMovies.map { entity ->
                    Movie(
                        id = entity.id,
                        title = entity.title,
                        overview = entity.overview,
                        poster_path = entity.posterPath.toString(),
                        release_date = entity.releaseDate,
                        vote_average = entity.voteAverage // Use stored value instead of default
                    )
                },
                total_pages = 1 // Offline mode shows cached movies only
            )
        }
    }


    suspend fun getMovieDetails(movieId: Int): MovieDetailsResponse {
        return if (isNetworkAvailable()) {
            val details = RetrofitClient.instance.getMovieDetails(movieId, apiKey = "24f4591904aa6cb41814de8604cb5e04")

            // Ensure correct handling of ID
            val movieEntity = MovieEntity(
                id = movieId,  // Explicitly setting the movie ID
                title = details.title,
                overview = details.overview,
                posterPath = details.poster_path,
                releaseDate = details.releaseDate,
                runtime = details.runtime,
                genres = details.genres.joinToString { it.name },
                voteAverage = details.voteAverage,
                revenue = details.revenue
            )
            movieDao.insertMovieDetails(movieEntity)

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
            id = movieId,  // Explicitly pass the movie ID
            title = details.title,
            overview = details.overview,
            posterPath = details.poster_path ?: "",
            releaseDate = details.releaseDate ?: "",
            runtime = details.runtime ?: 0,
            genres = details.genres.joinToString { it.name },
            voteAverage = details.voteAverage ?: 0.0F,
            revenue = details.revenue ?: 0L
        )
        movieDao.insertMovieDetails(movieEntity)
    }


}