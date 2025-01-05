package com.example.skycast

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.skycast.activities.LoginActivity
import com.example.skycast.api.Current
import com.example.skycast.api.NetworkResponse
import com.example.skycast.utils.getWeatherIcon
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherPage(viewModel: ViewModelWeather) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SkyCast",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 40.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                context.startActivity(Intent(context, LoginActivity::class.java))
                                (context as? Activity)?.finish()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                var city by remember { mutableStateOf("") }
                val weatherResult = viewModel.weatherResult.observeAsState()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 15.dp)
                                .height(60.dp),
                            value = city,
                            onValueChange = { city = it },
                            label = { Text(text = "Search for any Location") },
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search for any Location",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { viewModel.getData(city) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Button",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    when (val result = weatherResult.value) {
                        is NetworkResponse.Error -> {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(text = result.message, color = MaterialTheme.colorScheme.error)
                            }
                        }
                        NetworkResponse.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                        is NetworkResponse.Success -> {
                            val locationName = result.data.location.name
                            WeatherDetails(locationName = locationName, data = result.data.current)
                        }
                        null -> {}
                    }
                }
            }
        }
    )

}

@Composable
fun WeatherDetails(locationName: String, data: Current) {
    val animatedAlpha by animateFloatAsState(targetValue = 1f, label = "")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .alpha(animatedAlpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = locationName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val weatherIcon = getWeatherIcon(data.condition.text)
                Icon(
                    painter = painterResource(id = weatherIcon),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${data.temp_c}°C",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = data.condition.text,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            WeatherFeatureCard(
                label = "Humidity",
                value = "${data.humidity}%",
                modifier = Modifier.weight(1f)
            )
            WeatherFeatureCard(
                label = "Wind",
                value = "${data.wind_kph} km/h",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            WeatherFeatureCard(
                label = "Pressure",
                value = "${data.pressure_mb} hPa",
                modifier = Modifier.weight(1f)
            )
            WeatherFeatureCard(
                label = "Visibility",
                value = "${data.vis_km} km",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun WeatherFeatureCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}