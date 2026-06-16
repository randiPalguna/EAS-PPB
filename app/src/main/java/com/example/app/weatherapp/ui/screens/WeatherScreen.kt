package com.example.app.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.app.weatherapp.ui.components.DailyForecastItem
import com.example.app.weatherapp.ui.components.ForecastItem
import com.example.app.weatherapp.ui.components.WeatherDetailCard
import com.example.app.weatherapp.ui.viewmodel.WeatherViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onSearchClick: () -> Unit
) {
    val weather by viewModel.currentWeather.collectAsState()
    val isSaved by viewModel.isCurrentCitySaved.collectAsState()
    val defaultCity by viewModel.defaultCity.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = weather?.location?.name ?: "Loading...",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                },
                navigationIcon = {
                    val isDefault = weather?.location?.name == defaultCity
                    IconButton(onClick = { viewModel.setDefaultCity() }) {
                        Icon(
                            imageVector = if (isDefault) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Set Default",
                            tint = if (isDefault) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSaveCity() }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save",
                            tint = if (isSaved) Color(0xFFFF5252) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        weather?.let { data ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                // Mengatur padding atas dari TopAppBar dan padding bawah yang tipis agar tidak ada gap besar
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = 16.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                // INI KUNCI UTAMANYA: Mengatur jarak antar item secara otomatis sebesar 24.dp
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // BAGIAN 1: Suhu & Cuaca
                item {
                    AsyncImage(
                        model = "https:${data.current.condition.icon.replace("64x64", "128x128")}",
                        contentDescription = null,
                        modifier = Modifier.size(160.dp)
                    )
                    Text(
                        text = "${data.current.tempC.toInt()}°",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Thin,
                            fontSize = 100.sp
                        )
                    )
                    Text(
                        text = data.current.condition.text,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                    // Spacer 40.dp dihapus!
                }

                // BAGIAN 2: Humidity & Wind
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        WeatherDetailCard(
                            label = "Humidity",
                            value = "${data.current.humidity}%",
                            icon = Icons.Outlined.WaterDrop,
                            modifier = Modifier.weight(1f)
                        )
                        WeatherDetailCard(
                            label = "Wind",
                            value = "${data.current.windKph} km/h",
                            icon = Icons.Outlined.Air,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Spacer 24.dp dihapus!
                }

                // BAGIAN 3: Today Forecast
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "TODAY",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val currentHour = LocalDateTime.now().hour
                                data.forecast?.forecastday?.firstOrNull()?.hour?.let { hours ->
                                    items(hours) { hourData ->
                                        val timeStr = hourData.time.split(" ")[1]
                                        val hour = timeStr.split(":")[0].toInt()

                                        val displayTime = if (hour == currentHour) "Now" else {
                                            if (hour == 0) "12 AM" else if (hour < 12) "$hour AM" else if (hour == 12) "12 PM" else "${hour - 12} PM"
                                        }

                                        ForecastItem(
                                            time = displayTime,
                                            temp = hourData.tempC.toInt().toString(),
                                            iconUrl = hourData.condition.icon,
                                            isSelected = hour == currentHour
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // Spacer 24.dp dihapus!
                }

                // BAGIAN 4: 3-Day Forecast
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "3-DAY FORECAST",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            data.forecast?.forecastday?.forEach { dayForecast ->
                                val date = LocalDate.parse(dayForecast.date)
                                val dayName = if (date == LocalDate.now()) "Today" else date.format(DateTimeFormatter.ofPattern("EEE"))

                                DailyForecastItem(
                                    day = dayName,
                                    maxTemp = dayForecast.day.maxTempC.toInt().toString(),
                                    minTemp = dayForecast.day.minTempC.toInt().toString(),
                                    iconUrl = dayForecast.day.condition.icon
                                )
                                if (dayForecast != data.forecast.forecastday.last()) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                                }
                            }
                        }
                    }
                    // Spacer 32.dp dihapus!
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}