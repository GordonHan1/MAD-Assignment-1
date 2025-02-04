package com.it2161.dit99999x.PopCornMovie.ui.components

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.it2161.dit99999x.PopCornMovie.data.Movie
import com.it2161.dit99999x.PopCornMovie.data.MovieDetailsResponse
import com.it2161.dit99999x.PopCornMovie.data.MovieRepository
import com.it2161.dit99999x.PopCornMovie.data.MovieViewerApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LandingPageViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LandingPageViewModel(repository) as T
    }
}

class LandingPageViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    var movies by mutableStateOf<List<Movie>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var currentPage by mutableStateOf(1)
        private set

    var totalPages by mutableStateOf(1)
        private set

    var selectedFilter by mutableStateOf(MovieListType.POPULAR)
        private set

    var isOffline by mutableStateOf(false) // Track network status

    var allMoviesOffline: List<Movie> = emptyList() // Store full movie list offline

    /**
     * Update the selectedFilter and reset the page to 1
     */
    fun updateFilter(newFilter: MovieListType) {
        selectedFilter = newFilter
        currentPage = 1
        fetchMovies()
    }

    /**
     * Set a new page value and fetch the movies
     */
    fun setPage(page: Int) {
        currentPage = page
        fetchMovies()
    }

    /**
     * Fetch movies using the current filter & page
     */
    fun fetchMovies() {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isOffline) {
                    // Use cached movies when offline
                    val cachedMovies = repository.getMoviesByType(selectedFilter, currentPage).results
                        .filter { it.poster_path?.isNotEmpty() == true }  // Ensure image exists
                    movies = cachedMovies
                    allMoviesOffline = cachedMovies
                    totalPages = 1
                }  else {
                    // Add logging before API call
                    Log.d("MovieData", "Fetching movies for page $currentPage")

                    val response = repository.getMoviesByType(selectedFilter, currentPage)
                    // Add logging after API call
                    Log.d("MovieData", "API Response - Total Results: ${response.results.size}")

                    movies = response.results
                    totalPages = response.total_pages

                    // Add logging after assignment
                    Log.d("MovieData", "Updated movies list size: ${movies.size}")

                    allMoviesOffline = response.results

                    // Fetch movie details for each movie and store them locally
                    response.results.forEach { movie ->
                        fetchAndStoreMovieDetails(movie.id)
                    }
                }
            } catch (e: Exception) {
                // Improve error logging
                Log.e("MovieData", "Error fetching movies", e)
                Log.d("MovieData", "Fetched Movies: ${movies.size}")
            } finally {
                isLoading = false
            }
        }
    }


    fun fetchAndStoreMovieDetails(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val details = repository.getMovieDetails(movieId)
                repository.insertMovieDetails(details, movieId)  // Pass movieId explicitly
            } catch (e: Exception) {
                // Handle errors
            }
        }
    }

    fun searchMovies(query: String) {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.searchMovies(query)
                movies = response.results
                totalPages = response.total_pages
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    fun onMovieVisible(movie: Movie) {
        viewModelScope.launch {
            repository.cacheMovieWhenVisible(movie, currentPage)
        }
    }


}

