package com.it2161.dit99999x.PopCornMovie.data


data class MovieItem(
    val title: String,
    val director: String,
    val releaseDate: String,
    val ratings_score: Float,
    val actors: List<String>,
    val image: String,
    val genre: String,
    val length: Int,
    val synopsis: String,
    val comment: List<Comments>
)

data class Comments(
    val user: String,
    val comment: String
)


