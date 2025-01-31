package com.it2161.dit99999x.PopCornMovie.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.dit99999x.PopCornMovie.data.Movie
import com.it2161.dit99999x.PopCornMovie.data.MovieRepository
import com.it2161.dit99999x.PopCornMovie.ui.components.MovieListType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LandingPageViewModel : ViewModel() {

    init {
        fetchMovies() // fetch as soon as ViewModel is created
    }

    private val repository = MovieRepository() // or use DI/Hilt in a real project

    // State variables
    var movies by mutableStateOf<List<Movie>>(emptyList())
        private set

    var isLoading: Boolean = false
        private set

    var currentPage: Int = 1
        private set

    var totalPages: Int = 1
        private set

    var selectedFilter: MovieListType = MovieListType.POPULAR
        private set

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
                val response = repository.getMoviesByType(selectedFilter, currentPage)
                movies = response.results
                totalPages = response.total_pages
            } catch (e: Exception) {
                // Handle network or parsing errors
            } finally {
                isLoading = false
            }
        }
    }
}