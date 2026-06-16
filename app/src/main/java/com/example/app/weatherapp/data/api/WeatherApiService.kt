package com.example.app.weatherapp.data.api

import com.example.app.weatherapp.data.model.SearchResponse
import com.example.app.weatherapp.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("days") days: Int = 3,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): WeatherResponse

    @GET("search.json")
    suspend fun searchCity(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): List<SearchResponse>

    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("aqi") aqi: String = "no"
    ): WeatherResponse

    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"
    }
}
