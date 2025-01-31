package com.it2161.dit99999x.PopCornMovie.data

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val results: List<Movie>,
    val page: Int,
    val total_pages: Int
)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val vote_average: Float,
    val release_date: String
)

data class MovieDetailsResponse(
    val adult: Boolean,
    val genres: List<Genre>,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("release_date") val releaseDate: String,
    val runtime: Int?,
    @SerializedName("vote_count") val voteCount: Int,
    val title: String,
    val poster_path: String,
    @SerializedName("vote_average") val voteAverage: Float,
    val overview: String,
    val revenue: Long
)

data class Genre(
    val id: Int,
    val name: String
)