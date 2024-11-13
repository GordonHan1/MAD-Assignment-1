package com.it2161.dit99999x.PopCornMovie


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.it2161.dit99999x.PopCornMovie.ui.components.LandingPage

import com.it2161.dit99999x.PopCornMovie.ui.theme.Assignment1Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment1Theme {
                LandingPage()
            }

        }
    }

}





