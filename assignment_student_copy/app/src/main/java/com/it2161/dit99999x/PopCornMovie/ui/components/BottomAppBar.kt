package com.it2161.dit99999x.PopCornMovie.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.it2161.dit99999x.PopCornMovie.R

sealed class BottomNavItem(
    val route: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {
    object Home : BottomNavItem("LandingPage", R.drawable.home_opaque, R.drawable.home)
    object Middle : BottomNavItem("MiddleScreenRoute", R.drawable.home, R.drawable.home)
    object Profile : BottomNavItem("ProfileScreen", R.drawable.user_opaque, R.drawable.user)
}

@Composable
fun BottomAppBar(navController: NavController) {
    // Define your navigation items with their routes and icons

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Middle,
        BottomNavItem.Profile
    )

    // Get current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(modifier = Modifier.height(64.dp)) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Avoid multiple copies of the same destination
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (currentRoute == item.route) {
                                item.selectedIcon
                            } else {
                                item.unselectedIcon
                            }
                        ),
                        contentDescription = item.route,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = null
            )
        }
    }
}