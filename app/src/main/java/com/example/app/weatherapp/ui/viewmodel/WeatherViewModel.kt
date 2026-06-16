package com.example.app.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app.weatherapp.data.local.CityEntity
import com.example.app.weatherapp.data.model.SearchResponse
import com.example.app.weatherapp.data.model.WeatherResponse
import com.example.app.weatherapp.data.pref.PreferenceManager
import com.example.app.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _currentWeather = MutableStateFlow<WeatherResponse?>(null)
    val currentWeather: StateFlow<WeatherResponse?> = _currentWeather.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResponse>>(emptyList())
    val searchResults: StateFlow<List<SearchResponse>> = _searchResults.asStateFlow()

    private val _savedCities = MutableStateFlow<List<CityEntity>>(emptyList())
    val savedCities: StateFlow<List<CityEntity>> = _savedCities.asStateFlow()

    private val _isCurrentCitySaved = MutableStateFlow(false)
    val isCurrentCitySaved: StateFlow<Boolean> = _isCurrentCitySaved.asStateFlow()

    private val _defaultCity = MutableStateFlow("")
    val defaultCity: StateFlow<String> = _defaultCity.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observeSavedCities()
        loadDefaultCityAndFetchWeather()
    }

    private fun loadDefaultCityAndFetchWeather() {
        viewModelScope.launch {
            val city = preferenceManager.defaultCity.first()
            _defaultCity.value = city
            fetchWeather(city)
        }
    }

    private fun observeSavedCities() {
        viewModelScope.launch {
            repository.getSavedCities().collect {
                _savedCities.value = it
                checkIfCurrentCityIsSaved()
            }
        }
    }

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getForecast(city)
                _currentWeather.value = response
                checkIfCurrentCityIsSaved()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setDefaultCity() {
        val city = _currentWeather.value?.location?.name ?: return
        viewModelScope.launch {
            preferenceManager.setDefaultCity(city)
            _defaultCity.value = city
        }
    }

    fun searchCity(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                _searchResults.value = repository.searchCity(query)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleSaveCity() {
        val weather = _currentWeather.value ?: return
        viewModelScope.launch {
            val isSaved = repository.isCitySaved(weather.location.name)
            if (isSaved) {
                repository.deleteCity(
                    CityEntity(
                        weather.location.name,
                        weather.location.country,
                        weather.current.tempC,
                        weather.current.condition.text,
                        weather.current.condition.icon
                    )
                )
            } else {
                repository.saveCity(
                    CityEntity(
                        weather.location.name,
                        weather.location.country,
                        weather.current.tempC,
                        weather.current.condition.text,
                        weather.current.condition.icon
                    )
                )
            }
            checkIfCurrentCityIsSaved()
        }
    }

    fun deleteSavedCity(city: CityEntity) {
        viewModelScope.launch {
            repository.deleteCity(city)
        }
    }

    private suspend fun checkIfCurrentCityIsSaved() {
        val cityName = _currentWeather.value?.location?.name ?: return
        _isCurrentCitySaved.value = repository.isCitySaved(cityName)
    }

    class Factory(
        private val repository: WeatherRepository,
        private val preferenceManager: PreferenceManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WeatherViewModel(repository, preferenceManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
