package com.example.app.weatherapp.data.repository

import com.example.app.weatherapp.data.api.WeatherApiService
import com.example.app.weatherapp.data.local.CityDao
import com.example.app.weatherapp.data.local.CityEntity
import com.example.app.weatherapp.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class WeatherRepository(
    private val apiService: WeatherApiService,
    private val cityDao: CityDao,
    private val apiKey: String
) {
    suspend fun getForecast(city: String): WeatherResponse {
        return apiService.getForecast(apiKey, city)
    }

    suspend fun searchCity(query: String) = apiService.searchCity(apiKey, query)

    fun getSavedCities(): Flow<List<CityEntity>> = cityDao.getAllCities()

    suspend fun saveCity(city: CityEntity) = cityDao.insertCity(city)

    suspend fun deleteCity(city: CityEntity) = cityDao.deleteCity(city)

    suspend fun isCitySaved(cityName: String) = cityDao.isCitySaved(cityName)
    
    suspend fun getCurrentWeather(city: String) = apiService.getCurrentWeather(apiKey, city)
}
