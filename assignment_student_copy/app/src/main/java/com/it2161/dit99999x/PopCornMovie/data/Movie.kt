package com.it2161.dit99999x.PopCornMovie.data

data class MovieResponse(
    val results: List<Movie>
)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val vote_average: Float,
    val release_date: String
)