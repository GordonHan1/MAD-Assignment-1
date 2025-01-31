package com.it2161.dit99999x.PopCornMovie.ui.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    // Retrieve user info from SharedPreferences
    val userName = sharedPreferences.getString("username", "") ?: ""
    val email = sharedPreferences.getString("email", "") ?: ""
    val yob = sharedPreferences.getString("yob", "") ?: ""
    val updates = sharedPreferences.getBoolean("updates", false)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = userName, fontWeight = FontWeight.Bold) })

        } ,
        bottomBar = { BottomAppBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Your Profile",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            // Display user info
            Text(
                text = "Username: $userName",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Email: $email",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Subscribed to Updates: $updates",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Logout button
            Button(
                onClick = {
                    // Mark user as logged out
                    sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()

                    // Navigate back to LoginScreen
                    navController.navigate("LoginScreen") {
                        popUpTo("LoginScreen") { inclusive = true }
                    }
                }
            ) {
                Text(text = "Logout")
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navController = rememberNavController())
}
