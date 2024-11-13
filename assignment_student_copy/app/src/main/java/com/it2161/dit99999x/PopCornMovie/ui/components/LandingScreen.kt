@file:OptIn(ExperimentalMaterial3Api::class)

package com.it2161.dit99999x.PopCornMovie.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.it2161.dit99999x.PopCornMovie.MovieRaterApplication as MovieRaterApp


@Composable
fun LandingPage() {
    var showProfileMenu by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.weight(1f))
                        Text("PopCornMovie", textAlign = TextAlign.Center, modifier = Modifier.weight(2f))
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                            IconButton(onClick = { showProfileMenu = !showProfileMenu }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Overflow Menu")
                                DropdownMenu(expanded = showProfileMenu, onDismissRequest = { showProfileMenu = false }) {
                                    DropdownMenuItem(
                                        text = { Text("View Profile") },
                                        onClick = { /* Handle view profile click */ }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Logout") },
                                        onClick = { /* Handle logout click */ }
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            MovieItem("IntoTheUnknown", "Into The Unknown", "A thrilling journey beyond the realms of the known universe.", 4.5f)
            MovieItem("EchosOfEternity", "Echos of Eternity", "A deep dive into the echoes of timeless love and destiny.", 4.0f)
            MovieItem("LostInTime", "Lost in Time", "A mysterious adventure through different time periods.", 4.2f)
            MovieItem("ShadowsOfThePast", "Shadows of the Past", "A detective story that unveils hidden truths.", 3.8f)
            MovieItem("BeneathTheSurface", "Beneath The Surface", "A chilling thriller about secrets lying underwater.", 4.7f)
        }
    }
}

@Composable
fun MovieItem(imageFileName: String, title: String, synopsis: String, rating: Float) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        val movieRaterApp = MovieRaterApp()
        val image: Bitmap = movieRaterApp.getImgVector(imageFileName)
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .padding(end = 16.dp)
                .clickable(onClick = { /* TODO: Handle image click */ })
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 20.sp, modifier = Modifier.padding(bottom = 4.dp))
            Text(text = synopsis, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Rating: $rating/5", fontSize = 14.sp)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLandingPage() {
    LandingPage()
}




