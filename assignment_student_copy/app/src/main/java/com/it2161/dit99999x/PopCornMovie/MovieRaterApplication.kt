package com.it2161.dit99999x.PopCornMovie

//import com.it2161.dit99999x.assignment1.data.Comments
//import com.it2161.dit99999x.assignment1.data.MovieItem
//import com.it2161.dit99999x.assignment1.data.UserProfile
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.it2161.dit99999x.PopCornMovie.data.Comments
import com.it2161.dit99999x.PopCornMovie.data.MovieItem
import com.it2161.dit99999x.PopCornMovie.data.UserProfile
import com.it2161.dit99999x.PopCornMovie.data.mvBeneathTheSurface
import com.it2161.dit99999x.PopCornMovie.data.mvCityOfShadowsData
import com.it2161.dit99999x.PopCornMovie.data.mvEchosOfEternityData
import com.it2161.dit99999x.PopCornMovie.data.mvIntoTheUnknownData
import com.it2161.dit99999x.PopCornMovie.data.mvLostInTimeData
import com.it2161.dit99999x.PopCornMovie.data.mvShadowsOfthePastData
import com.it2161.dit99999x.PopCornMovie.data.mvTheLastFrontierData
import com.it2161.dit99999x.PopCornMovie.data.mvTheSilentStormData
import jsonData
import org.json.JSONArray
import java.io.File

class MovieRaterApplication : Application() {
    var data = mutableListOf<MovieItem>()
        get() = field
        set(value) {
            field = value
            saveListFile(applicationContext)
        }

    var userProfile: UserProfile? = null
        get() = field
        set(value) {
            if (value != null) {
                field = value
                saveProfileToFile(applicationContext)
            }

        }

    override fun onCreate() {
        super.onCreate()

        //Loads Profile and Movie details
        loadData(applicationContext)
    }

    //Loads the movie data into the list.
    private fun loadMovieDataIntoList(context: Context) {
        val jsonArray = JSONArray(jsonData)


        val file = File(context.filesDir, "movielist.dat")
        if (!file.exists()) {
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val title = jsonObject.getString("title")
                val director = jsonObject.getString("director")
                val releaseDate = jsonObject.getString("release_date")
                var rating = jsonObject.getString("rating")
                val parts = rating.split("/")
                if (parts.size > 2)
                    rating = parts[0]
                else
                    rating = "0"
                val poster = jsonObject.getString("image")
                val actors = jsonObject.getJSONArray("actors")
                val genre = jsonObject.getString("genre")
                val length = jsonObject.getInt("length")
                val synopsis = jsonObject.getString("synopsis")
                val comments = jsonObject.getJSONArray("comments")
                val listOfComments = mutableListOf<Comments>()
                for (i in 0 until comments.length()) {
                    val commentsObj = comments.getJSONObject(i)
                    val user = commentsObj.getString("user")
                    val comment = commentsObj.getString("comment")
                    val newComment = Comments(user, comment)
                    listOfComments.add(newComment)

                }
                data.add(
                    MovieItem(
                        title = title,
                        director = director,
                        releaseDate = releaseDate,
                        ratings_score = rating.toFloat(),
                        actors = actors.toString().split(","),
                        image = poster,
                        genre = genre,
                        length = length,
                        synopsis = synopsis,
                        comment = listOfComments
                    )
                )
            }
        } else {

            val file = File(context.filesDir, "movielist.dat")
            data = try {
                val jsonString = file.readText()
                val gson = Gson()
                val type = object :
                    TypeToken<List<MovieItem>>() {}.type // TypeToken reflects the updated Movie class
                gson.fromJson(jsonString, type)
            } catch (e: Exception) {
                mutableListOf<MovieItem>()
            }
        }


    }

    private fun saveListFile(context: Context) {


        if (context != null) {
            val gson = Gson()
            val jsonString = gson.toJson(data)
            val file = File(context.filesDir, "movielist.dat")
            file.writeText(jsonString)
        }

    }

    private fun saveProfileToFile(context: Context) {

        if (context != null) {
            val gson = Gson()
            val jsonString = gson.toJson(userProfile)
            val file = File(context.filesDir, "profile.dat")
            file.writeText(jsonString)
        }
    }

    private fun loadProfileFromFile(context: Context) {

        val file = File(context.filesDir, "profile.dat")
        userProfile =
            try {
                val jsonString = file.readText()
                val gson = Gson()
                val type = object :
                    TypeToken<UserProfile>() {}.type // TypeToken reflects the updated Movie class
                gson.fromJson(jsonString, type)
            } catch (e: Exception) {
                null
            }
    }

    private fun loadData(context: Context) {
        loadProfileFromFile(context)
        loadMovieDataIntoList(context)

    }

    //Gets image vector.
    fun getImgVector(fileName: String): Bitmap {

        val dataValue = when (fileName) {

            "IntoTheUnknown" -> mvIntoTheUnknownData
            "EchosOfEternity" -> mvEchosOfEternityData
            "LostInTime" -> mvLostInTimeData
            "ShadowsOfThePast" -> mvShadowsOfthePastData
            "BeneathTheSurface" -> mvBeneathTheSurface
            "LastFrontier" -> mvTheLastFrontierData
            "CityOfShadows" -> mvCityOfShadowsData
            "SilentStorm" -> mvTheSilentStormData
            else -> ""

        }

        val imageBytes = Base64.decode(
            dataValue,
            Base64.DEFAULT
        )
        val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        return imageBitmap
    }


}