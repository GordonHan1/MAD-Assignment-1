package com.it2161.dit99999x.PopCornMovie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val releaseDate: String,
    val page: Int, // Add page number to entity
    val runtime: Int,
    val genres: String?,
    val voteAverage: Float,
    val revenue: Long
)

