package com.example.app.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app.weatherapp.ui.navigation.Screen
import com.example.app.weatherapp.ui.viewmodel.WeatherViewModel

@Composable
fun MainScreen(viewModel: WeatherViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // MATIKAN MINIMUM TOUCH TARGET BAWAAN MATERIAL 3 DI SINI
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .navigationBarsPadding() // Melindungi dari gesture bar sistem
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            // Sekarang padding ini akan benar-benar akurat tanpa ada ruang tersembunyi
                            .padding(top = 12.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // --- WEATHER TAB ---
                        val weatherSelected = currentDestination?.hierarchy?.any { it.route == Screen.Weather.route } == true
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    navController.navigate(Screen.Weather.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = if (weatherSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else Color.Transparent,
                                modifier = Modifier.height(24.dp).width(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        if (weatherSelected) Icons.Filled.Cloud else Icons.Outlined.Cloud,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = if (weatherSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                "Weather",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (weatherSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // --- CITIES TAB ---
                        val citiesSelected = currentDestination?.hierarchy?.any { it.route == Screen.Cities.route } == true
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    navController.navigate(Screen.Cities.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = if (citiesSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else Color.Transparent,
                                modifier = Modifier.height(24.dp).width(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        if (citiesSelected) Icons.Filled.LocationCity else Icons.Outlined.LocationCity,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = if (citiesSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                "Cities",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (citiesSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Weather.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Weather.route) {
                WeatherScreen(
                    viewModel = viewModel,
                    onSearchClick = {
                        navController.navigate(Screen.Cities.route)
                    }
                )
            }
            composable(Screen.Cities.route) {
                CitiesScreen(
                    viewModel = viewModel,
                    onCitySelected = {
                        navController.navigate(Screen.Weather.route) {
                            popUpTo(Screen.Weather.route) { inclusive = false }
                        }
                    }
                )
            }
        }
    }
}