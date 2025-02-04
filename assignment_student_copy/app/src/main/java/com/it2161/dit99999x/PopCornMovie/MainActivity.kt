package com.it2161.dit99999x.PopCornMovie

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.it2161.dit99999x.PopCornMovie.ui.components.FavoritesScreen
import com.it2161.dit99999x.PopCornMovie.ui.components.LandingPage
import com.it2161.dit99999x.PopCornMovie.ui.components.LoginScreen
import com.it2161.dit99999x.PopCornMovie.ui.components.RegistrationScreen
import com.it2161.dit99999x.PopCornMovie.ui.theme.Assignment1Theme
import com.it2161.dit99999x.PopCornMovie.ui.components.ProfileScreen
import com.it2161.dit99999x.PopCornMovie.ui.components.MovieDetailScreen
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        enableEdgeToEdge()
        setContent {
            Assignment1Theme {
                MainScreen(isLoggedIn = isLoggedIn)
            }
        }
    }
}

@Composable
fun MainScreen(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        // Change start destination based on login state
        startDestination = if (isLoggedIn) "LandingPage" else "LoginScreen"
    ){  // Set LoginScreen as the start destination
        composable("LoginScreen") {
            LoginScreen(navController = navController)  // Pass navController to LoginScreen
        }
        composable("com.it2161.dit99999x.PopCornMovie.ui.components.RegistrationScreen") {
            RegistrationScreen(navController = navController)  // Pass navController to com.it2161.dit99999x.PopCornMovie.ui.components.RegistrationScreen
        }
        composable("LandingPage") {
            LandingPage(navController = navController)
        }
        composable("ProfileScreen") {
            ProfileScreen(navController)
        }
        composable("favorites") {
            FavoritesScreen(navController = navController)
        }
        composable(
            route = "movie_detail_screen/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            MovieDetailScreen(navController = navController, movieId = movieId)
        }
    }
}
