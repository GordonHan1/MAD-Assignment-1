package com.it2161.dit99999x.PopCornMovie.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.dit99999x.PopCornMovie.data.MovieDetailsResponse
import com.it2161.dit99999x.PopCornMovie.data.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val repository: MovieRepository = MovieRepository() // or inject it
) : ViewModel() {

    private val _movieDetails = MutableStateFlow<MovieDetailsResponse?>(null)
    val movieDetails = _movieDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val details = repository.getMovieDetails(movieId)
                _movieDetails.value = details
                // Fetch reviews after movie details
                fetchMovieReviews(movieId)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _reviews = MutableStateFlow<List<MovieReview>>(emptyList())
    val reviews = _reviews.asStateFlow()

    private val _isLoadingReviews = MutableStateFlow(false)
    val isLoadingReviews = _isLoadingReviews.asStateFlow()

    private val _reviewsError = MutableStateFlow<String?>(null)
    val reviewsError = _reviewsError.asStateFlow()

    private fun fetchMovieReviews(movieId: Int) {
        viewModelScope.launch {
            try {
                _isLoadingReviews.value = true
                _reviewsError.value = null
                val reviewsResponse = repository.getMovieReviews(movieId)
                _reviews.value = reviewsResponse.results
            } catch (e: Exception) {
                _reviewsError.value = e.message
            } finally {
                _isLoadingReviews.value = false
            }
        }
    }
}

data class MovieReview(
    val id: String,
    val author: String,
    val author_details: AuthorDetails?,
    val content: String,
    val createdAt: String,
    val rating: Float? = null
)

data class MovieReviewsResponse(
    val page: Int,
    val results: List<MovieReview>,
    val totalPages: Int,
    val totalResults: Int
)

data class AuthorDetails(
    val rating: Float?,
    val avatar_path: String?
    // ... other fields ...
)

