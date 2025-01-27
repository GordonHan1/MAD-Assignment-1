package com.it2161.dit99999x.PopCornMovie

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.it2161.dit99999x.PopCornMovie.ui.components.Comment
import com.it2161.dit99999x.PopCornMovie.ui.components.LandingPage
import com.it2161.dit99999x.PopCornMovie.ui.components.LoginScreen
import com.it2161.dit99999x.PopCornMovie.ui.components.RegistrationScreen
import com.it2161.dit99999x.PopCornMovie.ui.theme.Assignment1Theme
import com.it2161.dit99999x.PopCornMovie.ui.components.MovieDetailScreen
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment1Theme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "LoginScreen") {  // Set LoginScreen as the start destination
        composable("LoginScreen") {
            LoginScreen(navController = navController)  // Pass navController to LoginScreen
        }
        composable("com.it2161.dit99999x.PopCornMovie.ui.components.RegistrationScreen") {
            RegistrationScreen(navController = navController)  // Pass navController to com.it2161.dit99999x.PopCornMovie.ui.components.RegistrationScreen
        }
        composable("LandingScreen") {
            LandingPage(navController = navController)
        }
        composable(
            route = "MovieDetailScreen/{movieName}/{movieDetails}",
            arguments = listOf(
                navArgument("movieName") { type = NavType.StringType },
                navArgument("movieDetails") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val movieName = backStackEntry.arguments?.getString("movieName") ?: "Unknown"
            val movieDetails = backStackEntry.arguments?.getString("movieDetails") ?: "No details available"

            // Replace with an actual movie poster bitmap or resource
            val moviePoster = Bitmap.createBitmap(150, 200, Bitmap.Config.ARGB_8888) // Placeholder
            val comments = listOf(
                Comment("1", "User1", "Amazing movie!", Date(Date().time - 3600 * 1000)),
                Comment("2", "User2", "Loved the visuals!", Date(Date().time - 7200 * 1000)),
                Comment("3", "User3", "Great story!", Date(Date().time - 172800 * 1000))
            )

            MovieDetailScreen(
                navController = navController,
                movieName = movieName,
                moviePoster = moviePoster,
                movieDetails = movieDetails,
                comments = comments
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Assignment1Theme {
        MainScreen()
    }
}

