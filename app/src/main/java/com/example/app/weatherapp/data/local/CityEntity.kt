package com.example.app.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_cities")
data class CityEntity(
    @PrimaryKey val name: String,
    val country: String,
    val tempC: Double,
    val condition: String,
    val iconUrl: String
)
