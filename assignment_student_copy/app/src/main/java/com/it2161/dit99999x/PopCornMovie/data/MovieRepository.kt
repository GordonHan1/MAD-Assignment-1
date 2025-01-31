package com.it2161.dit99999x.PopCornMovie.data
import com.it2161.dit99999x.PopCornMovie.ui.components.MovieListType
import com.it2161.dit99999x.PopCornMovie.ui.components.MovieReviewsResponse

class MovieRepository {
    suspend fun getMoviesByType(type: MovieListType, page: Int): MovieResponse {
        return when (type) {
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
    }

    suspend fun getMovieDetails(movieId: Int): MovieDetailsResponse {
        return RetrofitClient.instance.getMovieDetails(
            movieId = movieId,
            apiKey = "24f4591904aa6cb41814de8604cb5e04"
        )
    }
    suspend fun getMovieReviews(movieId: Int): MovieReviewsResponse {
        return RetrofitClient.instance.getMovieReviews(
            movieId = movieId,
            apiKey = "24f4591904aa6cb41814de8604cb5e04")
    }

}
