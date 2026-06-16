package com.example.app.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM saved_cities")
    fun getAllCities(): Flow<List<CityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity)

    @Delete
    suspend fun deleteCity(city: CityEntity)

    @Query("SELECT EXISTS(SELECT * FROM saved_cities WHERE name = :cityName)")
    suspend fun isCitySaved(cityName: String): Boolean

    @Query("SELECT * FROM saved_cities WHERE name = :cityName LIMIT 1")
    suspend fun getCityByName(cityName: String): CityEntity?
}
