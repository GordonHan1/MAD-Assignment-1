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
import com.it2161.dit99999x.PopCornMovie.data.MovieDatabase
import com.it2161.dit99999x.PopCornMovie.data.MovieRepository
import com.it2161.dit99999x.PopCornMovie.data.UserProfile
import com.it2161.dit99999x.PopCornMovie.data.mvBeneathTheSurface
import com.it2161.dit99999x.PopCornMovie.data.mvCityOfShadowsData
import com.it2161.dit99999x.PopCornMovie.data.mvEchosOfEternityData
import com.it2161.dit99999x.PopCornMovie.data.mvIntoTheUnknownData
import com.it2161.dit99999x.PopCornMovie.data.mvLostInTimeData
import com.it2161.dit99999x.PopCornMovie.data.mvShadowsOfthePastData
import com.it2161.dit99999x.PopCornMovie.data.mvTheLastFrontierData
import com.it2161.dit99999x.PopCornMovie.data.mvTheSilentStormData
import org.json.JSONArray
import java.io.File

class MovieRaterApplication : Application() {
    lateinit var database: MovieDatabase
        private set

    val repository: MovieRepository by lazy {
        database = MovieDatabase.getDatabase(this)
        MovieRepository(database.movieDao(), this)
    }

    override fun onCreate() {
        super.onCreate()
        repository
    }

    var userProfile: UserProfile? = null
        get() = field
        set(value) {
            if (value != null) {
                field = value
                saveProfileToFile(applicationContext)
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