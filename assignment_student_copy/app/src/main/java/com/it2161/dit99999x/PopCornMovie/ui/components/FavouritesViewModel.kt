package com.it2161.dit99999x.PopCornMovie.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.it2161.dit99999x.PopCornMovie.data.Movie
import com.it2161.dit99999x.PopCornMovie.data.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    val favorites = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _favorites.value = repository.getFavorites()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            if (repository.isFavorite(movie.id)) {
                repository.removeFromFavorites(movie.id)
            } else {
                repository.addToFavorites(movie)
            }
            loadFavorites()
        }
    }

    fun isFavorite(movieId: Int): Boolean {
        var result = false
        viewModelScope.launch {
            result = repository.isFavorite(movieId)
        }
        return result
    }
}

class FavoritesViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(repository) as T
    }
}