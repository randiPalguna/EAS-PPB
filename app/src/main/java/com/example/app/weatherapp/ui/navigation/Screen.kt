package com.example.app.weatherapp.ui.navigation

sealed class Screen(val route: String) {
    object Weather : Screen("weather")
    object Cities : Screen("cities")
}
