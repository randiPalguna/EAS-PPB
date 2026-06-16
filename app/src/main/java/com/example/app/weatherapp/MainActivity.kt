package com.example.app.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.app.weatherapp.data.api.WeatherApiService
import com.example.app.weatherapp.data.local.WeatherDatabase
import com.example.app.weatherapp.data.pref.PreferenceManager
import com.example.app.weatherapp.data.repository.WeatherRepository
import com.example.app.weatherapp.ui.screens.MainScreen
import com.example.app.weatherapp.ui.theme.WeatherAppTheme
import com.example.app.weatherapp.ui.viewmodel.WeatherViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize dependencies
        val database = WeatherDatabase.getDatabase(this)
        val preferenceManager = PreferenceManager(this)
        val retrofit = Retrofit.Builder()
            .baseUrl(WeatherApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(WeatherApiService::class.java)
        
        // WeatherAPI.com API Key
        val apiKey = BuildConfig.WEATHER_API_KEY
        
        val repository = WeatherRepository(apiService, database.cityDao(), apiKey)
        val viewModelFactory = WeatherViewModel.Factory(repository, preferenceManager)
        val viewModel = ViewModelProvider(this, viewModelFactory)[WeatherViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
